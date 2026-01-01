package com.sps.nurul_ikhlas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Village;

@Repository
public interface VillageRepository extends JpaRepository<Village, String> {
    List<Village> findByDistrictId(String districtId);
}
