package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.payload.request.AcademicYearRequest;

public interface AdminService {
    // Student management
    String verifyStudent(String studentId);

    List<Student> getRegisteredStudents();

    // Academic Year CRUD
    AcademicYear createAcademicYear(AcademicYearRequest request);

    AcademicYear updateAcademicYear(String id, AcademicYearRequest request);

    List<AcademicYear> getAllAcademicYears();

    AcademicYear getActiveAcademicYear();

    void deleteAcademicYear(String id);
}
