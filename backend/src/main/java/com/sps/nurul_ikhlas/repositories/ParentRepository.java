package com.sps.nurul_ikhlas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Parent;

@Repository
public interface ParentRepository extends JpaRepository<Parent, String> {
    List<Parent> findByStudentId(String studentId);

    Optional<Parent> findByHandphone(String handphone);

    boolean existsByHandphone(String handphone);
}
