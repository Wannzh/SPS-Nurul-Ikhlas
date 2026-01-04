package com.sps.nurul_ikhlas.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${xendit.webhook-verification-token}")
    private String webhookVerificationToken;

    /**
     * Xendit Webhook Callback Handler
     * This endpoint receives payment notifications from Xendit
     */
    @PostMapping("/xendit-callback")
    public ResponseEntity<ApiResponse<Void>> handleXenditCallback(
            @RequestHeader(value = "x-callback-token", required = false) String callbackToken,
            @RequestBody Map<String, Object> payload) {

        log.info("Received Xendit callback: {}", payload);

        // Verify webhook token
        if (webhookVerificationToken != null && !webhookVerificationToken.isEmpty()
                && !webhookVerificationToken.equals("YOUR_WEBHOOK_VERIFICATION_TOKEN")) {
            if (callbackToken == null || !callbackToken.equals(webhookVerificationToken)) {
                log.warn("Invalid callback token received");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid callback token"));
            }
        }

        try {
            String invoiceId = (String) payload.get("id");
            String status = (String) payload.get("status");

            if (invoiceId == null || status == null) {
                log.warn("Missing required fields in callback payload");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Missing required fields"));
            }

            paymentService.handleWebhookCallback(invoiceId, status);

            return ResponseEntity.ok(ApiResponse.success("Callback processed successfully"));

        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process callback: " + e.getMessage()));
        }
    }
}
