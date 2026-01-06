package com.sps.nurul_ikhlas.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.entities.UniformOrderItem;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.PaymentStatus;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UniformOrderRepository;
import com.sps.nurul_ikhlas.repositories.UniformRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
import com.sps.nurul_ikhlas.services.StudentTransactionService;

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

    @Override
    public List<Uniform> getAvailableUniforms() {
        return uniformRepository.findByStockGreaterThan(0);
    }

    @Override
    @Transactional
    public UniformOrder createUniformOrder(String parentUsername, CreateUniformOrderRequest request) {
        // 1. Validate Parent User and get Student
        User user = userRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        if (user.getPerson() == null) {
            throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
        }

        Student student = studentRepository.findByPersonId(user.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));

        // 2. Create Order
        UniformOrder order = UniformOrder.builder()
                .student(student)
                .orderDate(LocalDateTime.now())
                .totalAmount(0.0)
                .totalPaid(0.0)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();

        Double totalAmount = 0.0;

        // 3. Process Items
        for (CreateUniformOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Uniform uniform = uniformRepository.findById(itemReq.getUniformId())
                    .orElseThrow(() -> new RuntimeException("Seragam tidak ditemukan: " + itemReq.getUniformId()));

            // Check stock
            if (uniform.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException(
                        "Stok tidak cukup untuk: " + uniform.getName() + " (Tersedia: " + uniform.getStock() + ")");
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

            // Decrease stock
            uniform.setStock(uniform.getStock() - itemReq.getQuantity());
            uniformRepository.save(uniform);
            log.info("Stock decreased for uniform {}: {} -> {}", uniform.getName(),
                    uniform.getStock() + itemReq.getQuantity(), uniform.getStock());
        }

        order.setTotalAmount(totalAmount);
        uniformOrderRepository.save(order);

        log.info("Created uniform order for student {} with total: {}", student.getPerson().getFullName(), totalAmount);

        return order;
    }

    @Override
    public List<UniformOrder> getMyUniformOrders(String parentUsername) {
        User user = userRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        if (user.getPerson() == null) {
            throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
        }

        Student student = studentRepository.findByPersonId(user.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));

        return uniformOrderRepository.findByStudentIdOrderByOrderDateDesc(student.getId());
    }
}
