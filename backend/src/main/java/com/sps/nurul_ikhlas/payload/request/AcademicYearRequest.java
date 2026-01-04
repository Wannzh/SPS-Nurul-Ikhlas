package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AcademicYearRequest {
    @NotBlank(message = "Nama tahun ajaran wajib diisi")
    private String name;

    @NotNull(message = "Status wajib diisi")
    private AcademicYearStatus status;

    @NotNull(message = "Biaya pendaftaran wajib diisi")
    @Positive(message = "Biaya pendaftaran harus lebih dari 0")
    private Double registrationFee;
}
