package com.sps.nurul_ikhlas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.UniformOrder;

@Repository
public interface UniformOrderRepository extends JpaRepository<UniformOrder, String> {
    List<UniformOrder> findByStudentIdOrderByOrderDateDesc(String studentId);
}
