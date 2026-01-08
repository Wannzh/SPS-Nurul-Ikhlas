package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.BillCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthlyPaymentRequest {
    @NotNull(message = "Kategori tagihan wajib diisi")
    private BillCategory billCategory;

    @NotNull(message = "Jumlah bulan wajib diisi")
    @Min(value = 1, message = "Minimal 1 bulan")
    private Integer numberOfMonths = 1;
}
