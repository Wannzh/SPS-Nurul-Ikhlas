package com.sps.nurul_ikhlas.models.entities;

import java.time.LocalDate;

import com.sps.nurul_ikhlas.models.enums.Relation;
import com.sps.nurul_ikhlas.models.enums.Religion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "parents")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parent {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private Relation relation;
    private String name;

    private String birthPlace;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Religion religion;
    private String citizenship;

    @Column(name = "last_education")
    private String lastEducation;
    private String job;
    private String address;

    @Column(length = 20)
    private String handphone;

    private String email;
}
