package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.enums.BillCategory;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.payload.request.PayBillRequest;
import com.sps.nurul_ikhlas.payload.response.MonthlyBillDetailResponse;
import com.sps.nurul_ikhlas.payload.response.MonthlyStatusResponse;
import com.sps.nurul_ikhlas.payload.response.SppInfoResponse;

public interface StudentTransactionService {
    // Uniform Ordering
    List<Uniform> getAvailableUniforms();

    UniformOrder createUniformOrder(String parentUsername, CreateUniformOrderRequest request);

    List<UniformOrder> getMyUniformOrders(String parentUsername);

    // SPP Payment (Legacy)
    SppInfoResponse getSppInfo(String parentUsername);

    PaymentTransaction createSppPayment(String parentUsername, Integer months) throws Exception;

    List<PaymentTransaction> getSppHistory(String parentUsername);

    // Monthly Infaq/Kas Payment (Summary - Legacy)
    MonthlyStatusResponse getMonthlyStatus(String parentUsername);

    PaymentTransaction createMonthlyPayment(String parentUsername, BillCategory category, Integer months)
            throws Exception;

    List<PaymentTransaction> getMonthlyPaymentHistory(String parentUsername, BillCategory category);

    // Monthly Infaq/Kas Payment (Detailed - New)
    MonthlyBillDetailResponse getMonthlyBillDetails(String parentUsername);

    PaymentTransaction paySelectedBills(String parentUsername, PayBillRequest request) throws Exception;
}
