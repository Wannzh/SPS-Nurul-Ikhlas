package com.sps.nurul_ikhlas.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.PaymentStatus;
import com.sps.nurul_ikhlas.models.enums.PaymentType;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.models.enums.TransactionStatus;
import com.sps.nurul_ikhlas.payload.request.PaymentRequest;
import com.sps.nurul_ikhlas.repositories.PaymentTransactionRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UniformOrderRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
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
    private final UserRepository userRepository;
    private final UniformOrderRepository uniformOrderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

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
        params.put("invoice_duration", 86400);
        params.put("success_redirect_url", successRedirectUrl);
        params.put("failure_redirect_url", failureRedirectUrl);

        Invoice invoice = Invoice.create(params);
        log.info("Created Xendit Invoice: {} for student: {}", invoice.getId(), student.getId());

        return invoice;
    }

    @Override
    @Transactional
    public PaymentTransaction createPayment(String parentUsername, PaymentRequest request) throws Exception {
        // 1. Validate User and get Student
        User user = userRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        if (user.getPerson() == null) {
            throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
        }

        Student student = studentRepository.findByPersonId(user.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));

        // 2. Validate based on payment type
        String description;
        String externalId;

        if (request.getPaymentType() == PaymentType.UNIFORM) {
            UniformOrder order = uniformOrderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

            Double remaining = order.getTotalAmount() - order.getTotalPaid();
            if (request.getAmount() > remaining) {
                throw new RuntimeException("Jumlah pembayaran melebihi sisa tagihan (Max: " + remaining + ")");
            }

            description = "Pembayaran Seragam - " + student.getPerson().getFullName();
            externalId = "UNIFORM-" + order.getId() + "-" + System.currentTimeMillis();
        } else {
            description = "Pembayaran " + request.getPaymentType().name() + " - " + student.getPerson().getFullName();
            externalId = request.getPaymentType().name() + "-" + student.getId() + "-" + System.currentTimeMillis();
        }

        // 3. Create Xendit Invoice
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", externalId);
        params.put("amount", request.getAmount());
        params.put("description", description);
        params.put("invoice_duration", 86400);
        params.put("success_redirect_url", successRedirectUrl);
        params.put("failure_redirect_url", failureRedirectUrl);

        Invoice invoice = Invoice.create(params);
        log.info("Created payment invoice: {} for amount: {}", invoice.getId(), request.getAmount());

        // 4. Create PaymentTransaction record
        PaymentTransaction transaction = PaymentTransaction.builder()
                .student(student)
                .orderId(request.getOrderId())
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .xenditInvoiceId(invoice.getId())
                .xenditPaymentUrl(invoice.getInvoiceUrl())
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        paymentTransactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public List<PaymentTransaction> getPaymentHistory(String parentUsername) {
        User user = userRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        if (user.getPerson() == null) {
            throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
        }

        Student student = studentRepository.findByPersonId(user.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));

        return paymentTransactionRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
    }

    @Override
    @Transactional
    public void handleWebhookCallback(String invoiceId, String status) {
        log.info("Received webhook callback - Invoice: {}, Status: {}", invoiceId, status);

        // First check if this is a PaymentTransaction (new system)
        PaymentTransaction transaction = paymentTransactionRepository.findByXenditInvoiceId(invoiceId)
                .orElse(null);

        if (transaction != null) {
            handlePaymentTransactionWebhook(transaction, status);
            return;
        }

        // Fallback to legacy Student payment handling
        Student student = studentRepository.findAll().stream()
                .filter(s -> invoiceId.equals(s.getXenditInvoiceId()))
                .findFirst()
                .orElse(null);

        if (student == null) {
            log.warn("No transaction or student found for invoice: {}", invoiceId);
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

    private void handlePaymentTransactionWebhook(PaymentTransaction transaction, String status) {
        if ("PAID".equalsIgnoreCase(status) || "SETTLED".equalsIgnoreCase(status)) {
            transaction.setStatus(TransactionStatus.PAID);

            // If this is a UNIFORM payment, update the order
            if (transaction.getPaymentType() == PaymentType.UNIFORM && transaction.getOrderId() != null) {
                UniformOrder order = uniformOrderRepository.findById(transaction.getOrderId())
                        .orElse(null);

                if (order != null) {
                    Double newTotalPaid = order.getTotalPaid() + transaction.getAmount();
                    order.setTotalPaid(newTotalPaid);

                    if (newTotalPaid >= order.getTotalAmount()) {
                        order.setPaymentStatus(PaymentStatus.PAID);
                    } else {
                        order.setPaymentStatus(PaymentStatus.PARTIAL);
                    }

                    uniformOrderRepository.save(order);
                    log.info("Updated order {} - TotalPaid: {}, Status: {}", order.getId(), newTotalPaid,
                            order.getPaymentStatus());
                }
            }
        } else if ("EXPIRED".equalsIgnoreCase(status)) {
            transaction.setStatus(TransactionStatus.EXPIRED);
        }

        paymentTransactionRepository.save(transaction);
        log.info("Updated transaction {} status to {}", transaction.getId(), transaction.getStatus());
    }
}
