package com.sps.nurul_ikhlas.models.entities;

import java.time.LocalDate;
import java.util.List;

import com.sps.nurul_ikhlas.models.enums.AgeGroup;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(unique = true, nullable = true)
    private String nisn;

    @Column(name = "child_order")
    private Integer childOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;

    @Column(name = "register_date")
    private LocalDate registerDate;

    private String photo;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Parent> parents;

    // Payment fields
    @Column(name = "xendit_invoice_id")
    private String xenditInvoiceId;

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "payment_status")
    private String paymentStatus;

    // Document fields
    @Column(name = "doc_kk_path")
    private String docKkPath;

    @Column(name = "doc_akta_path")
    private String docAktaPath;

    @Column(name = "doc_ktp_path")
    private String docKtpPath;
}
