package com.sps.nurul_ikhlas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Province;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
}
