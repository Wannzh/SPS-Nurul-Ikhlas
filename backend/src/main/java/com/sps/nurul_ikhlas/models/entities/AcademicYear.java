package com.sps.nurul_ikhlas.models.entities;

import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "batch")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear {
    @Id
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private AcademicYearStatus status;

    @Column(name = "registration_fee")
    private Double registrationFee;
}
