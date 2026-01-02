package com.sps.nurul_ikhlas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByPersonId(String personId);
}
