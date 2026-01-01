package com.sps.nurul_ikhlas.models.entities;

import java.time.LocalDate;

import com.sps.nurul_ikhlas.models.enums.StudentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "students")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private People person;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private AcademicYear batch;

    @ManyToOne
    @JoinColumn(name = "current_class_id")
    private SchoolClass currentClass;

    @Column(unique = true, nullable = false)
    private String nisn;

    @Column(name = "register_date")
    private LocalDate registerDate;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    private Double height;
    private Double weight;
}
