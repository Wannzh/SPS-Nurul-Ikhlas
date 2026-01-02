package com.sps.nurul_ikhlas.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.services.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

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
}
