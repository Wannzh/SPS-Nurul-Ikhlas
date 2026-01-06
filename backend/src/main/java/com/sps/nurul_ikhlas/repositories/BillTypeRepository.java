package com.sps.nurul_ikhlas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.BillType;

@Repository
public interface BillTypeRepository extends JpaRepository<BillType, String> {
}
