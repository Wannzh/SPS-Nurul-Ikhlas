package com.sps.nurul_ikhlas.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.services.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/students/registered")
    public ResponseEntity<List<Student>> getRegisteredStudents() {
        List<Student> students = adminService.getRegisteredStudents();
        return ResponseEntity.ok(students);
    }

    @PostMapping("/verify/{studentId}")
    public ResponseEntity<Map<String, String>> verifyStudent(@PathVariable String studentId) {
        String message = adminService.verifyStudent(studentId);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
