package com.sps.nurul_ikhlas.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.BillType;
import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.enums.OrderStatus;
import com.sps.nurul_ikhlas.models.enums.PaymentType;
import com.sps.nurul_ikhlas.models.enums.Period;
import com.sps.nurul_ikhlas.models.enums.TransactionStatus;
import com.sps.nurul_ikhlas.payload.response.StudentArrearsResponse;
import com.sps.nurul_ikhlas.repositories.BillTypeRepository;
import com.sps.nurul_ikhlas.repositories.PaymentTransactionRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UniformOrderRepository;
import com.sps.nurul_ikhlas.services.AdminTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTransactionServiceImpl implements AdminTransactionService {

    private final UniformOrderRepository uniformOrderRepository;
    private final StudentRepository studentRepository;
    private final BillTypeRepository billTypeRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public List<UniformOrder> getAllUniformOrders(OrderStatus status) {
        if (status != null) {
            return uniformOrderRepository.findByOrderStatusOrderByOrderDateDesc(status);
        }
        return uniformOrderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    @Transactional
    public UniformOrder updateOrderStatus(String orderId, OrderStatus status) {
        UniformOrder order = uniformOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        order.setOrderStatus(status);
        uniformOrderRepository.save(order);

        log.info("Updated order {} status to {}", orderId, status);
        return order;
    }

    @Override
    public List<StudentArrearsResponse> getStudentArrears() {
        List<StudentArrearsResponse> result = new ArrayList<>();

        // Get SPP BillType (Monthly)
        BillType sppBillType = billTypeRepository.findByPeriod(Period.MONTHLY)
                .stream()
                .findFirst()
                .orElse(null);

        if (sppBillType == null) {
            log.warn("No monthly bill type (SPP) configured");
            return result;
        }

        Double sppAmount = sppBillType.getAmount();
        List<Student> students = studentRepository.findAll();

        for (Student student : students) {
            if (student.getRegisterDate() == null) {
                continue;
            }

            // Calculate months since registration
            long monthsActive = ChronoUnit.MONTHS.between(
                    student.getRegisterDate().withDayOfMonth(1),
                    LocalDate.now().withDayOfMonth(1)) + 1; // Include current month

            // Get paid SPP transactions
            List<PaymentTransaction> sppPayments = paymentTransactionRepository
                    .findByStudentIdOrderByCreatedAtDesc(student.getId())
                    .stream()
                    .filter(t -> t.getPaymentType() == PaymentType.SPP && t.getStatus() == TransactionStatus.PAID)
                    .toList();

            // Count months paid based on total amount
            Double totalPaidSpp = sppPayments.stream()
                    .mapToDouble(PaymentTransaction::getAmount)
                    .sum();
            int monthsPaid = (int) Math.floor(totalPaidSpp / sppAmount);

            int monthsUnpaid = (int) Math.max(0, monthsActive - monthsPaid);
            Double totalArrears = monthsUnpaid * sppAmount;

            if (monthsUnpaid > 0) {
                result.add(StudentArrearsResponse.builder()
                        .studentId(student.getId())
                        .studentName(student.getPerson() != null ? student.getPerson().getFullName() : "Unknown")
                        .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : "-")
                        .monthsUnpaid(monthsUnpaid)
                        .totalArrears(totalArrears)
                        .sppAmount(sppAmount)
                        .build());
            }
        }

        // Sort by arrears descending
        result.sort((a, b) -> b.getTotalArrears().compareTo(a.getTotalArrears()));

        return result;
    }
}
