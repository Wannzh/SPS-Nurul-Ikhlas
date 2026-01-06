package com.sps.nurul_ikhlas.controllers;

import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.DocumentType;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
import com.sps.nurul_ikhlas.utils.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StudentDocumentController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @PostMapping("/parent/documents/upload")
    @PreAuthorize("hasRole('ORTU')")
    public ResponseEntity<ApiResponse<String>> uploadDocument(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam("docType") DocumentType docType) {

        try {
            // Validate file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Ukuran file maksimal 2MB"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Format file harus gambar atau PDF"));
            }

            // Get student
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            Student student = studentRepository.findByPersonId(user.getPerson().getId())
                    .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan"));

            // Store file
            String filePath = fileStorageService.storeFile(file, student.getId(), docType.name());

            // Update student document path
            switch (docType) {
                case KK:
                    // Delete old file if exists
                    if (student.getDocKkPath() != null) {
                        fileStorageService.deleteFile(student.getDocKkPath());
                    }
                    student.setDocKkPath(filePath);
                    break;
                case AKTA:
                    if (student.getDocAktaPath() != null) {
                        fileStorageService.deleteFile(student.getDocAktaPath());
                    }
                    student.setDocAktaPath(filePath);
                    break;
                case KTP:
                    if (student.getDocKtpPath() != null) {
                        fileStorageService.deleteFile(student.getDocKtpPath());
                    }
                    student.setDocKtpPath(filePath);
                    break;
            }

            studentRepository.save(student);
            log.info("Document {} uploaded for student {}", docType, student.getId());

            return ResponseEntity.ok(ApiResponse.success("Dokumen berhasil diupload", filePath));

        } catch (Exception e) {
            log.error("Failed to upload document", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Gagal mengupload dokumen: " + e.getMessage()));
        }
    }

    @GetMapping("/parent/documents/status")
    @PreAuthorize("hasRole('ORTU')")
    public ResponseEntity<ApiResponse<DocumentStatusResponse>> getDocumentStatus(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Student student = studentRepository.findByPersonId(user.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan"));

        DocumentStatusResponse status = new DocumentStatusResponse(
                student.getDocKkPath() != null,
                student.getDocAktaPath() != null,
                student.getDocKtpPath() != null,
                student.getDocKkPath(),
                student.getDocAktaPath(),
                student.getDocKtpPath());

        return ResponseEntity.ok(ApiResponse.success("Status dokumen", status));
    }

    @GetMapping("/documents/{studentId}/{filename}")
    @PreAuthorize("hasAnyRole('ORTU', 'ADMIN')")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String studentId,
            @PathVariable String filename,
            Principal principal) {

        try {
            // Validate access (owner or admin)
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // If not admin, verify ownership
            if (!user.getRole().name().equals("ADMIN")) {
                Student student = studentRepository.findByPersonId(user.getPerson().getId())
                        .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan"));
                if (!student.getId().equals(studentId)) {
                    return ResponseEntity.status(403).build();
                }
            }

            String filePath = studentId + "/" + filename;
            Resource resource = fileStorageService.loadFile(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to download document", e);
            return ResponseEntity.notFound().build();
        }
    }

    // Inner class for response
    public record DocumentStatusResponse(
            boolean kkUploaded,
            boolean aktaUploaded,
            boolean ktpUploaded,
            String kkPath,
            String aktaPath,
            String ktpPath) {
    }
}
