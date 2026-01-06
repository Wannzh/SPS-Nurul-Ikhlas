package com.sps.nurul_ikhlas.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentDashboardResponse {
    private String studentId;
    private String studentName;
    private String nisn;
    private String status;
    private String currentClass;
    private String academicYear;
    private String photo;
    private String gender;
    private String birthPlace;
    private String birthDate;
}
