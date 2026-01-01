package com.sps.nurul_ikhlas.models.entities;

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
@Table(name = "villages")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Village {
    @Id
    private String id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
}
