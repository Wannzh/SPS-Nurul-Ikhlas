package com.sps.nurul_ikhlas.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.enums.OrderStatus;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.payload.response.StudentArrearsResponse;
import com.sps.nurul_ikhlas.services.AdminTransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    @GetMapping("/orders/uniform")
    public ResponseEntity<ApiResponse<List<UniformOrder>>> getAllUniformOrders(
            @RequestParam(required = false) OrderStatus status) {
        List<UniformOrder> orders = adminTransactionService.getAllUniformOrders(status);
        return ResponseEntity.ok(ApiResponse.success("Daftar pesanan seragam", orders));
    }

    @PutMapping("/orders/uniform/{orderId}/status")
    public ResponseEntity<ApiResponse<UniformOrder>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        UniformOrder order = adminTransactionService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Status pesanan berhasil diperbarui", order));
    }

    @GetMapping("/finance/arrears")
    public ResponseEntity<ApiResponse<List<StudentArrearsResponse>>> getStudentArrears() {
        List<StudentArrearsResponse> arrears = adminTransactionService.getStudentArrears();
        return ResponseEntity.ok(ApiResponse.success("Daftar tunggakan siswa", arrears));
    }
}
