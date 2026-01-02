package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.Student;

public interface AdminService {
    String verifyStudent(String studentId);

    List<Student> getRegisteredStudents();
}
