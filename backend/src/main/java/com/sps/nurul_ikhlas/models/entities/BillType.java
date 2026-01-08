package com.sps.nurul_ikhlas.models.entities;

import com.sps.nurul_ikhlas.models.enums.BillCategory;
import com.sps.nurul_ikhlas.models.enums.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "bill_types")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Period period = Period.MONTHLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillCategory category;

    private String description;
}
