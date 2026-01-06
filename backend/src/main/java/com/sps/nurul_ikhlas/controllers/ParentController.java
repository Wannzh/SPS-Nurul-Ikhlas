package com.sps.nurul_ikhlas.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.payload.response.ParentDashboardResponse;
import com.sps.nurul_ikhlas.services.ParentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORTU')")
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/my-data")
    public ResponseEntity<ApiResponse<ParentDashboardResponse>> getMyData(Principal principal) {
        ParentDashboardResponse response = parentService.getMyData(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Data berhasil diambil", response));
    }
}
