package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.BillCategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BillTypeRequest {
    @NotNull(message = "Kategori tagihan wajib diisi")
    private BillCategory category;

    @NotNull(message = "Jumlah wajib diisi")
    @Positive(message = "Jumlah harus lebih dari 0")
    private Double amount;

    private String description;
}
