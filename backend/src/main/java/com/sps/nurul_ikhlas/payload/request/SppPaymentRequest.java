package com.sps.nurul_ikhlas.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SppPaymentRequest {
    @NotNull(message = "Jumlah bulan wajib diisi")
    @Min(value = 1, message = "Minimal 1 bulan")
    private Integer months;
}
