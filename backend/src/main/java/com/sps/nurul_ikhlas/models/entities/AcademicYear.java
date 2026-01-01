package com.sps.nurul_ikhlas.models.entities;

import jakarta.persistence.Entity;
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

}
