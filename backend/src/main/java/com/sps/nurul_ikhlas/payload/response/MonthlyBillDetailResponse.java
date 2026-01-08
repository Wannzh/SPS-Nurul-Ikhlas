package com.sps.nurul_ikhlas.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBillDetailResponse {
    private List<MonthlyBillItem> infaqItems;
    private List<MonthlyBillItem> kasItems;
    private Double infaqMonthlyFee;
    private Double kasMonthlyFee;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyBillItem {
        private String month; // Format: "2024-01"
        private String monthLabel; // Format: "Januari 2024"
        private String status; // PAID, DUE, ARREARS
        private Double amount;
        private String paidAt; // ISO date string if paid
        private String transactionId; // if paid
    }
}
