package com.sps.nurul_ikhlas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatusResponse {
    // Infaq Status
    private Double infaqMonthlyFee;
    private Integer infaqMonthsPaid;
    private Integer infaqMonthsUnpaid;
    private Double infaqTotalArrears;
    private boolean infaqIsDue;
    private boolean infaqIsCritical;

    // Kas Status
    private Double kasMonthlyFee;
    private Integer kasMonthsPaid;
    private Integer kasMonthsUnpaid;
    private Double kasTotalArrears;
    private boolean kasIsDue;
    private boolean kasIsCritical;

    // Common
    private Integer totalMonthsActive;
}
