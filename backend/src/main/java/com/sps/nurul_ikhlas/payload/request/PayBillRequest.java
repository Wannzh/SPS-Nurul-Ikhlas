package com.sps.nurul_ikhlas.payload.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PayBillRequest {
    @NotEmpty(message = "Minimal 1 item harus dipilih")
    private List<BillItem> items;

    @Data
    public static class BillItem {
        private String category; // INFAQ or KAS
        private String month; // Format: "2024-01"
    }
}
