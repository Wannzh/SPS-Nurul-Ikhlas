package com.sps.nurulikhlas.models.entities;

import com.sps.nurulikhlas.models.enums.Role;

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
}
