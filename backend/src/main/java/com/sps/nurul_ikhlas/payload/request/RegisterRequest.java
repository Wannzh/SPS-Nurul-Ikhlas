package com.sps.nurul_ikhlas.payload.request;

import java.time.LocalDate;

import com.sps.nurul_ikhlas.models.enums.Gender;
import com.sps.nurul_ikhlas.models.enums.Religion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    // Child Info
    @NotBlank(message = "Nama lengkap anak wajib diisi")
    private String childFullName;

    @NotBlank(message = "Tempat lahir wajib diisi")
    private String birthPlace;

    @NotNull(message = "Tanggal lahir wajib diisi")
    private LocalDate birthDate;

    @NotNull(message = "Jenis kelamin wajib diisi")
    private Gender gender;

    @NotNull(message = "Agama wajib diisi")
    private Religion religion;

    // Parent Info
    @NotBlank(message = "Nama ayah wajib diisi")
    private String fatherName;

    @NotBlank(message = "Nama ibu wajib diisi")
    private String motherName;

    private String parentJob;

    @NotBlank(message = "Nomor telepon wajib diisi")
    private String phoneNumber;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    // Address
    private String address;

    @NotBlank(message = "Provinsi wajib diisi")
    private String provinceId;

    @NotBlank(message = "Kabupaten/Kota wajib diisi")
    private String regencyId;

    @NotBlank(message = "Kecamatan wajib diisi")
    private String districtId;

    @NotBlank(message = "Desa/Kelurahan wajib diisi")
    private String villageId;

    // Agreement
    @NotNull(message = "Persetujuan wajib dicentang")
    private Boolean isAgreed;
}
