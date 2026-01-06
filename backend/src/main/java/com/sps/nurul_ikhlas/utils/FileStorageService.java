package com.sps.nurul_ikhlas.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads/documents}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Upload directory initialized: {}", this.uploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    /**
     * Store a file for a student
     * 
     * @param file      The uploaded file
     * @param studentId The student's ID
     * @param docType   Document type (KK, AKTA, KTP)
     * @return Relative path to the stored file
     */
    public String storeFile(MultipartFile file, String studentId, String docType) throws IOException {
        // Validate file
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new RuntimeException("Invalid file path: " + originalFilename);
        }

        // Get file extension
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // Create student directory
        Path studentDir = this.uploadDir.resolve(studentId);
        Files.createDirectories(studentDir);

        // Generate unique filename
        String newFilename = docType + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetPath = studentDir.resolve(newFilename);

        // Copy file
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("File stored: {} for student: {}", newFilename, studentId);

        // Return relative path
        return studentId + "/" + newFilename;
    }

    /**
     * Load a file as Resource
     * 
     * @param filePath Relative path (studentId/filename)
     * @return Resource for the file
     */
    public Resource loadFile(String filePath) throws MalformedURLException {
        Path file = this.uploadDir.resolve(filePath).normalize();
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File not found: " + filePath);
        }
    }

    /**
     * Delete a file
     * 
     * @param filePath Relative path
     */
    public void deleteFile(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty())
            return;
        Path file = this.uploadDir.resolve(filePath).normalize();
        Files.deleteIfExists(file);
        log.info("File deleted: {}", filePath);
    }
}
