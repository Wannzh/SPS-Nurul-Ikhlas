package com.sps.nurul_ikhlas.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SppInfoResponse {
    private Double monthlyFee;
    private Integer totalMonthsActive;
    private Integer totalMonthsPaid;
    private Integer monthsUnpaidCount;
    private Double totalArrears;
}
