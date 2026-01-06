package com.sps.nurul_ikhlas.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "uniform_order_items")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniformOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private UniformOrder order;

    @ManyToOne
    @JoinColumn(name = "uniform_id", nullable = false)
    private Uniform uniform;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_moment", nullable = false)
    private Double priceAtMoment;

    @Column(name = "sub_total", nullable = false)
    private Double subTotal;
}
