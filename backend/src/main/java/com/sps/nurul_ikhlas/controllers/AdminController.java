package com.sps.nurul_ikhlas.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.payload.request.AcademicYearRequest;
import com.sps.nurul_ikhlas.services.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // =========================================
    // STUDENT MANAGEMENT
    // =========================================

    @GetMapping("/students/registered")
    public ResponseEntity<ApiResponse<List<Student>>> getRegisteredStudents() {
        List<Student> students = adminService.getRegisteredStudents();
        return ResponseEntity.ok(ApiResponse.success("Data siswa yang terdaftar", students));
    }

    @PostMapping("/verify/{studentId}")
    public ResponseEntity<ApiResponse<Void>> verifyStudent(@PathVariable String studentId) {
        String message = adminService.verifyStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // =========================================
    // ACADEMIC YEAR CRUD
    // =========================================

    @PostMapping("/academic-years")
    public ResponseEntity<ApiResponse<AcademicYear>> createAcademicYear(
            @Valid @RequestBody AcademicYearRequest request) {
        AcademicYear academicYear = adminService.createAcademicYear(request);
        return ResponseEntity.ok(ApiResponse.success("Tahun ajaran berhasil dibuat", academicYear));
    }

    @PutMapping("/academic-years/{id}")
    public ResponseEntity<ApiResponse<AcademicYear>> updateAcademicYear(
            @PathVariable String id,
            @Valid @RequestBody AcademicYearRequest request) {
        AcademicYear academicYear = adminService.updateAcademicYear(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tahun ajaran berhasil diperbarui", academicYear));
    }

    @GetMapping("/academic-years")
    public ResponseEntity<ApiResponse<List<AcademicYear>>> getAllAcademicYears() {
        List<AcademicYear> academicYears = adminService.getAllAcademicYears();
        return ResponseEntity.ok(ApiResponse.success("Daftar tahun ajaran", academicYears));
    }

    @GetMapping("/academic-years/active")
    public ResponseEntity<ApiResponse<AcademicYear>> getActiveAcademicYear() {
        AcademicYear academicYear = adminService.getActiveAcademicYear();
        return ResponseEntity.ok(ApiResponse.success("Tahun ajaran aktif", academicYear));
    }

    @DeleteMapping("/academic-years/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAcademicYear(@PathVariable String id) {
        adminService.deleteAcademicYear(id);
        return ResponseEntity.ok(ApiResponse.success("Tahun ajaran berhasil dihapus"));
    }
}
