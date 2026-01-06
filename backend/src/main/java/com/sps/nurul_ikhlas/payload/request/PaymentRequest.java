package com.sps.nurul_ikhlas.payload.request;

import com.sps.nurul_ikhlas.models.enums.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;

    @NotNull(message = "Jumlah pembayaran wajib diisi")
    @Positive(message = "Jumlah harus lebih dari 0")
    private Double amount;

    @NotNull(message = "Tipe pembayaran wajib diisi")
    private PaymentType paymentType;
}
