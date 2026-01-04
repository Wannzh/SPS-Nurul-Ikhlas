package com.sps.nurul_ikhlas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String studentId;
    private String studentName;
    private String status;
    private String invoiceUrl;
}
