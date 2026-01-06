package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.payload.request.PaymentRequest;
import com.xendit.model.Invoice;

public interface PaymentService {
    Invoice createInvoice(Student student, Parent parent, Double amount) throws Exception;

    void handleWebhookCallback(String invoiceId, String status);

    // New methods for installment payments
    PaymentTransaction createPayment(String parentUsername, PaymentRequest request) throws Exception;

    List<PaymentTransaction> getPaymentHistory(String parentUsername);
}
