package com.sps.nurul_ikhlas.models.entities;

import java.time.LocalDate;

import com.sps.nurul_ikhlas.models.enums.Status;
import com.sps.nurul_ikhlas.models.enums.Blood;
import com.sps.nurul_ikhlas.models.enums.Gender;
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
@Table(name = "peoples")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class People {
    @Id
    @Column
    private String id;
    private String fullName;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birthPlace;
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "village_id")
    private Village village;
    private String address;

    @Enumerated(EnumType.STRING)
    private Religion religion;
    private String citizenship;
    private Integer children; 
    private Integer siblings;

    @Column(name = "half_siblings")
    private Integer halfSiblings;

    @Column(name = "adopt_siblings")
    private Integer adoptSiblings;

    @Enumerated(EnumType.STRING)
    @Column(name = "orphan_status") 
    private Status orphanStatus;

    private String everydayLanguange;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private Blood bloodType;

    private String disease;
    private String immunization;
    
    @Column(name = "special_characteristics")
    private String specialCharacteristics;
}
