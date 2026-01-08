package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.payload.response.SppInfoResponse;

public interface StudentTransactionService {
    // Uniform Ordering
    List<Uniform> getAvailableUniforms();

    UniformOrder createUniformOrder(String parentUsername, CreateUniformOrderRequest request);

    List<UniformOrder> getMyUniformOrders(String parentUsername);

    // SPP Payment
    SppInfoResponse getSppInfo(String parentUsername);

    PaymentTransaction createSppPayment(String parentUsername, Integer months) throws Exception;

    List<PaymentTransaction> getSppHistory(String parentUsername);
}
