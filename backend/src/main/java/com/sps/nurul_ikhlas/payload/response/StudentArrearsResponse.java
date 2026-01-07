package com.sps.nurul_ikhlas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentArrearsResponse {
    private String studentId;
    private String studentName;
    private String className;
    private Integer monthsUnpaid;
    private Double totalArrears;
    private Double sppAmount;
}
