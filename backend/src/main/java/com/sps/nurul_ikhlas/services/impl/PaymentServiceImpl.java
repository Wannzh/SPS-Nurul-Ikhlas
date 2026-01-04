package com.sps.nurul_ikhlas.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.services.PaymentService;
import com.xendit.Xendit;
import com.xendit.model.Invoice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final StudentRepository studentRepository;

    @Value("${xendit.api-key}")
    private String xenditApiKey;

    @Value("${xendit.success-redirect-url}")
    private String successRedirectUrl;

    @Value("${xendit.failure-redirect-url}")
    private String failureRedirectUrl;

    @PostConstruct
    public void init() {
        Xendit.apiKey = xenditApiKey;
        log.info("Xendit API initialized");
    }

    @Override
    public Invoice createInvoice(Student student, Parent parent, Double amount) throws Exception {
        String externalId = "PPDB-" + student.getId();
        String description = "Pendaftaran PPDB - " + student.getPerson().getFullName();
        String payerEmail = parent.getEmail();

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", externalId);
        params.put("amount", amount);
        params.put("payer_email", payerEmail);
        params.put("description", description);
        params.put("invoice_duration", 86400); // 24 hours
        params.put("success_redirect_url", successRedirectUrl);
        params.put("failure_redirect_url", failureRedirectUrl);

        Invoice invoice = Invoice.create(params);
        log.info("Created Xendit Invoice: {} for student: {}", invoice.getId(), student.getId());

        return invoice;
    }

    @Override
    public void handleWebhookCallback(String invoiceId, String status) {
        log.info("Received webhook callback - Invoice: {}, Status: {}", invoiceId, status);

        Student student = studentRepository.findAll().stream()
                .filter(s -> invoiceId.equals(s.getXenditInvoiceId()))
                .findFirst()
                .orElse(null);

        if (student == null) {
            log.warn("Student not found for invoice: {}", invoiceId);
            return;
        }

        student.setPaymentStatus(status);

        if ("PAID".equalsIgnoreCase(status) || "SETTLED".equalsIgnoreCase(status)) {
            student.setStatus(StudentStatus.REGISTERED);
            log.info("Student {} payment confirmed. Status updated to REGISTERED.", student.getId());
        } else if ("EXPIRED".equalsIgnoreCase(status)) {
            student.setPaymentStatus("EXPIRED");
            log.info("Student {} payment expired.", student.getId());
        }

        studentRepository.save(student);
    }
}
