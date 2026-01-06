package com.sps.nurul_ikhlas.models.entities;

import com.sps.nurul_ikhlas.models.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "users")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column
    private String id;

    private String username;
    private String password;
    private String image;

    @OneToOne
    @JoinColumn(name = "person_id")
    private People person;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "is_password_set")
    @Builder.Default
    private Boolean isPasswordSet = false;
}
