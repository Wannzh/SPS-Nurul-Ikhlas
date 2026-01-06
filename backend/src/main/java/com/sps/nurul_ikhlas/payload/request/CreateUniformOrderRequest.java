package com.sps.nurul_ikhlas.payload.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateUniformOrderRequest {
    @NotEmpty(message = "Items tidak boleh kosong")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private String uniformId;
        private Integer quantity;
    }
}
