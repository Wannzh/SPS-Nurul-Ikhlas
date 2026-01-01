package com.sps.nurul_ikhlas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findByRegencyId(String regencyId);
}
