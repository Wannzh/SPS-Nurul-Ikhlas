package com.sps.nurul_ikhlas.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetupPasswordRequest {
    @NotBlank(message = "Token wajib diisi")
    private String token;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String newPassword;
}
