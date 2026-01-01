package com.sps.nurulikhlas.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "regencies")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Regency {
    @Id
    private String id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;
}
