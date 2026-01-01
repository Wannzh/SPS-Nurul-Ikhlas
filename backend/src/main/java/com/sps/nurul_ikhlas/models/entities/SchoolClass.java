package com.sps.nurul_ikhlas.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "school_class")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass {
    @Id
    private String id;

    private String name;

    private Integer quota;
}
