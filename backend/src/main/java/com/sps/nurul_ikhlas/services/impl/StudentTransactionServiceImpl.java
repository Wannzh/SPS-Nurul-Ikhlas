package com.sps.nurul_ikhlas.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.BillType;
import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.entities.UniformOrderItem;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.PaymentStatus;
import com.sps.nurul_ikhlas.models.enums.PaymentType;
import com.sps.nurul_ikhlas.models.enums.Period;
import com.sps.nurul_ikhlas.models.enums.TransactionStatus;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.payload.response.SppInfoResponse;
import com.sps.nurul_ikhlas.repositories.BillTypeRepository;
import com.sps.nurul_ikhlas.repositories.PaymentTransactionRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UniformOrderRepository;
import com.sps.nurul_ikhlas.repositories.UniformRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
import com.sps.nurul_ikhlas.services.StudentTransactionService;
import com.xendit.Xendit;
import com.xendit.model.Invoice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentTransactionServiceImpl implements StudentTransactionService {

        private final UserRepository userRepository;
        private final StudentRepository studentRepository;
        private final UniformRepository uniformRepository;
        private final UniformOrderRepository uniformOrderRepository;
        private final BillTypeRepository billTypeRepository;
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
        }

        private Student getStudentFromUsername(String parentUsername) {
                User user = userRepository.findByUsername(parentUsername)
                                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

                if (user.getPerson() == null) {
                        throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
                }

                return studentRepository.findByPersonId(user.getPerson().getId())
                                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));
        }

        @Override
        public List<Uniform> getAvailableUniforms() {
                return uniformRepository.findByStockGreaterThan(0);
        }

        @Override
        @Transactional
        public UniformOrder createUniformOrder(String parentUsername, CreateUniformOrderRequest request) {
                Student student = getStudentFromUsername(parentUsername);

                UniformOrder order = UniformOrder.builder()
                                .student(student)
                                .orderDate(LocalDateTime.now())
                                .totalAmount(0.0)
                                .totalPaid(0.0)
                                .paymentStatus(PaymentStatus.UNPAID)
                                .build();

                Double totalAmount = 0.0;

                for (CreateUniformOrderRequest.OrderItemRequest itemReq : request.getItems()) {
                        Uniform uniform = uniformRepository.findById(itemReq.getUniformId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Seragam tidak ditemukan: " + itemReq.getUniformId()));

                        if (uniform.getStock() < itemReq.getQuantity()) {
                                throw new RuntimeException(
                                                "Stok tidak cukup untuk: " + uniform.getName() + " (Tersedia: "
                                                                + uniform.getStock() + ")");
                        }

                        Double subTotal = uniform.getPrice() * itemReq.getQuantity();
                        totalAmount += subTotal;

                        UniformOrderItem orderItem = UniformOrderItem.builder()
                                        .order(order)
                                        .uniform(uniform)
                                        .quantity(itemReq.getQuantity())
                                        .priceAtMoment(uniform.getPrice())
                                        .subTotal(subTotal)
                                        .build();

                        order.getItems().add(orderItem);

                        uniform.setStock(uniform.getStock() - itemReq.getQuantity());
                        uniformRepository.save(uniform);
                        log.info("Stock decreased for uniform {}: {} -> {}", uniform.getName(),
                                        uniform.getStock() + itemReq.getQuantity(), uniform.getStock());
                }

                order.setTotalAmount(totalAmount);
                uniformOrderRepository.save(order);

                log.info("Created uniform order for student {} with total: {}", student.getPerson().getFullName(),
                                totalAmount);

                return order;
        }

        @Override
        public List<UniformOrder> getMyUniformOrders(String parentUsername) {
                Student student = getStudentFromUsername(parentUsername);
                return uniformOrderRepository.findByStudentIdOrderByOrderDateDesc(student.getId());
        }

        // SPP Payment Methods
        @Override
        public SppInfoResponse getSppInfo(String parentUsername) {
                Student student = getStudentFromUsername(parentUsername);

                BillType sppBillType = billTypeRepository.findByPeriod(Period.MONTHLY)
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Biaya SPP belum dikonfigurasi"));

                Double monthlyFee = sppBillType.getAmount();

                // Calculate months active since registration
                int totalMonthsActive = 0;
                if (student.getRegisterDate() != null) {
                        totalMonthsActive = (int) ChronoUnit.MONTHS.between(
                                        student.getRegisterDate().withDayOfMonth(1),
                                        LocalDate.now().withDayOfMonth(1)) + 1;
                }

                // Calculate months paid from transaction history
                List<PaymentTransaction> sppPayments = paymentTransactionRepository
                                .findByStudentIdOrderByCreatedAtDesc(student.getId())
                                .stream()
                                .filter(t -> t.getPaymentType() == PaymentType.SPP
                                                && t.getStatus() == TransactionStatus.PAID)
                                .toList();

                Double totalPaidAmount = sppPayments.stream()
                                .mapToDouble(PaymentTransaction::getAmount)
                                .sum();

                int totalMonthsPaid = (int) Math.floor(totalPaidAmount / monthlyFee);
                int monthsUnpaidCount = Math.max(0, totalMonthsActive - totalMonthsPaid);
                Double totalArrears = monthsUnpaidCount * monthlyFee;

                return SppInfoResponse.builder()
                                .monthlyFee(monthlyFee)
                                .totalMonthsActive(totalMonthsActive)
                                .totalMonthsPaid(totalMonthsPaid)
                                .monthsUnpaidCount(monthsUnpaidCount)
                                .totalArrears(totalArrears)
                                .build();
        }

        @Override
        @Transactional
        public PaymentTransaction createSppPayment(String parentUsername, Integer months) throws Exception {
                Student student = getStudentFromUsername(parentUsername);

                BillType sppBillType = billTypeRepository.findByPeriod(Period.MONTHLY)
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Biaya SPP belum dikonfigurasi"));

                Double amount = sppBillType.getAmount() * months;
                String description = "Pembayaran SPP " + months + " bulan - " + student.getPerson().getFullName();
                String externalId = "SPP-" + student.getId() + "-" + System.currentTimeMillis();

                // Create Xendit Invoice
                Map<String, Object> params = new HashMap<>();
                params.put("external_id", externalId);
                params.put("amount", amount);
                params.put("description", description);
                params.put("invoice_duration", 86400);
                params.put("success_redirect_url", successRedirectUrl);
                params.put("failure_redirect_url", failureRedirectUrl);

                Invoice invoice = Invoice.create(params);
                log.info("Created SPP invoice: {} for {} months, amount: {}", invoice.getId(), months, amount);

                // Save transaction record
                PaymentTransaction transaction = PaymentTransaction.builder()
                                .student(student)
                                .paymentType(PaymentType.SPP)
                                .amount(amount)
                                .xenditInvoiceId(invoice.getId())
                                .xenditPaymentUrl(invoice.getInvoiceUrl())
                                .status(TransactionStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();

                paymentTransactionRepository.save(transaction);

                return transaction;
        }

        @Override
        public List<PaymentTransaction> getSppHistory(String parentUsername) {
                Student student = getStudentFromUsername(parentUsername);
                return paymentTransactionRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                                .stream()
                                .filter(t -> t.getPaymentType() == PaymentType.SPP)
                                .toList();
        }
}
