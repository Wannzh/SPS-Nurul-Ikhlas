package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.Period;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BillTypeRequest {
    @NotBlank(message = "Nama tagihan wajib diisi")
    private String name;

    @NotNull(message = "Jumlah wajib diisi")
    @Positive(message = "Jumlah harus lebih dari 0")
    private Double amount;

    @NotNull(message = "Periode wajib diisi")
    private Period period;

    private String description;
}
