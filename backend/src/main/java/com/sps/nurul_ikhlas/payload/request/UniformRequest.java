package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.UniformSize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UniformRequest {
    @NotBlank(message = "Nama seragam wajib diisi")
    private String name;

    @NotNull(message = "Ukuran wajib diisi")
    private UniformSize size;

    @NotNull(message = "Harga wajib diisi")
    @Positive(message = "Harga harus lebih dari 0")
    private Double price;

    @NotNull(message = "Stok wajib diisi")
    @PositiveOrZero(message = "Stok tidak boleh negatif")
    private Integer stock;

    private String description;
}
