package com.sps.nurul_ikhlas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Uniform;

@Repository
public interface UniformRepository extends JpaRepository<Uniform, String> {
    List<Uniform> findByStockGreaterThan(Integer stock);
}
