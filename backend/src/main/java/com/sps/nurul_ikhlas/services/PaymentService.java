package com.sps.nurul_ikhlas.services;

import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.xendit.model.Invoice;

public interface PaymentService {
    Invoice createInvoice(Student student, Parent parent, Double amount) throws Exception;

    void handleWebhookCallback(String invoiceId, String status);
}
