package com.sps.nurul_ikhlas.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.models.enums.BillCategory;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.payload.request.MonthlyPaymentRequest;
import com.sps.nurul_ikhlas.payload.request.PaymentRequest;
import com.sps.nurul_ikhlas.payload.request.SppPaymentRequest;
import com.sps.nurul_ikhlas.payload.response.MonthlyStatusResponse;
import com.sps.nurul_ikhlas.payload.response.SppInfoResponse;
import com.sps.nurul_ikhlas.services.PaymentService;
import com.sps.nurul_ikhlas.services.StudentTransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORTU')")
public class ParentTransactionController {

    private final StudentTransactionService transactionService;
    private final PaymentService paymentService;

    @GetMapping("/uniforms")
    public ResponseEntity<ApiResponse<List<Uniform>>> getAvailableUniforms() {
        List<Uniform> uniforms = transactionService.getAvailableUniforms();
        return ResponseEntity.ok(ApiResponse.success("Daftar seragam tersedia", uniforms));
    }

    @PostMapping("/orders/uniform")
    public ResponseEntity<ApiResponse<UniformOrder>> createUniformOrder(
            Principal principal,
            @Valid @RequestBody CreateUniformOrderRequest request) {
        UniformOrder order = transactionService.createUniformOrder(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Pesanan seragam berhasil dibuat", order));
    }

    @GetMapping("/orders/uniform")
    public ResponseEntity<ApiResponse<List<UniformOrder>>> getMyUniformOrders(Principal principal) {
        List<UniformOrder> orders = transactionService.getMyUniformOrders(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Riwayat pesanan seragam", orders));
    }

    // Payment Endpoints
    @PostMapping("/payments/create")
    public ResponseEntity<ApiResponse<PaymentTransaction>> createPayment(
            Principal principal,
            @Valid @RequestBody PaymentRequest request) throws Exception {
        PaymentTransaction transaction = paymentService.createPayment(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Invoice pembayaran berhasil dibuat", transaction));
    }

    @GetMapping("/payments/history")
    public ResponseEntity<ApiResponse<List<PaymentTransaction>>> getPaymentHistory(Principal principal) {
        List<PaymentTransaction> transactions = paymentService.getPaymentHistory(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Riwayat transaksi", transactions));
    }

    // SPP Endpoints (Legacy)
    @GetMapping("/finance/spp-info")
    public ResponseEntity<ApiResponse<SppInfoResponse>> getSppInfo(Principal principal) {
        SppInfoResponse info = transactionService.getSppInfo(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Informasi SPP", info));
    }

    @PostMapping("/payments/spp")
    public ResponseEntity<ApiResponse<PaymentTransaction>> createSppPayment(
            Principal principal,
            @Valid @RequestBody SppPaymentRequest request) throws Exception {
        PaymentTransaction transaction = transactionService.createSppPayment(principal.getName(), request.getMonths());
        return ResponseEntity.ok(ApiResponse.success("Invoice SPP berhasil dibuat", transaction));
    }

    @GetMapping("/payments/spp-history")
    public ResponseEntity<ApiResponse<List<PaymentTransaction>>> getSppHistory(Principal principal) {
        List<PaymentTransaction> transactions = transactionService.getSppHistory(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Riwayat pembayaran SPP", transactions));
    }

    // Monthly Infaq/Kas Endpoints (New)
    @GetMapping("/finance/monthly-status")
    public ResponseEntity<ApiResponse<MonthlyStatusResponse>> getMonthlyStatus(Principal principal) {
        MonthlyStatusResponse status = transactionService.getMonthlyStatus(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Status tagihan bulanan", status));
    }

    @PostMapping("/payments/monthly")
    public ResponseEntity<ApiResponse<PaymentTransaction>> createMonthlyPayment(
            Principal principal,
            @Valid @RequestBody MonthlyPaymentRequest request) throws Exception {
        PaymentTransaction transaction = transactionService.createMonthlyPayment(
                principal.getName(), request.getBillCategory(), request.getNumberOfMonths());
        return ResponseEntity.ok(ApiResponse.success("Invoice tagihan bulanan berhasil dibuat", transaction));
    }

    @GetMapping("/payments/monthly-history")
    public ResponseEntity<ApiResponse<List<PaymentTransaction>>> getMonthlyHistory(
            Principal principal,
            @RequestParam BillCategory category) {
        List<PaymentTransaction> transactions = transactionService.getMonthlyPaymentHistory(principal.getName(),
                category);
        return ResponseEntity.ok(ApiResponse.success("Riwayat pembayaran " + category, transactions));
    }
}
