package com.sps.nurul_ikhlas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationToken(String verificationToken);

    Boolean existsByUsername(String username);
}
