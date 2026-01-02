package com.sps.nurul_ikhlas.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
