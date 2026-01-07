package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.enums.OrderStatus;
import com.sps.nurul_ikhlas.payload.response.StudentArrearsResponse;

public interface AdminTransactionService {
    List<UniformOrder> getAllUniformOrders(OrderStatus status);

    UniformOrder updateOrderStatus(String orderId, OrderStatus status);

    List<StudentArrearsResponse> getStudentArrears();
}
