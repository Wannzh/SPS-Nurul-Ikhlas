package com.sps.nurul_ikhlas.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.UniformOrder;
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.payload.request.CreateUniformOrderRequest;
import com.sps.nurul_ikhlas.services.StudentTransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORTU')")
public class ParentTransactionController {

    private final StudentTransactionService transactionService;

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
}
