package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.BillType;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.payload.request.AcademicYearRequest;
import com.sps.nurul_ikhlas.payload.request.BillTypeRequest;
import com.sps.nurul_ikhlas.payload.request.UniformRequest;

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

    // Bill Type CRUD
    BillType createBillType(BillTypeRequest request);

    BillType updateBillType(String id, BillTypeRequest request);

    List<BillType> getAllBillTypes();

    void deleteBillType(String id);

    // Uniform CRUD
    Uniform createUniform(UniformRequest request);

    Uniform updateUniform(String id, UniformRequest request);

    List<Uniform> getAllUniforms();

    void deleteUniform(String id);
}
