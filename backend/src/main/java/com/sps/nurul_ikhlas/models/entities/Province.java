package com.sps.nurulikhlas.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "provinces")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    @Id
    private String id;
    private String name;
}
