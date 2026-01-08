package com.sps.nurul_ikhlas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.BillType;
import com.sps.nurul_ikhlas.models.enums.BillCategory;
import com.sps.nurul_ikhlas.models.enums.Period;

@Repository
public interface BillTypeRepository extends JpaRepository<BillType, String> {
    List<BillType> findByPeriod(Period period);

    Optional<BillType> findByCategory(BillCategory category);
}
