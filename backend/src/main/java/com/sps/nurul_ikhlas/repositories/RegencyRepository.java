package com.sps.nurul_ikhlas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Regency;

@Repository
public interface RegencyRepository extends JpaRepository<Regency, String> {
    List<Regency> findByProvinceId(String provinceId);
}
