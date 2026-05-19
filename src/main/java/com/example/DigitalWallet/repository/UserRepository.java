package com.example.DigitalWallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.DigitalWallet.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findById(UUID id);
    Optional<User> findByNationalId(String nationalId);
    Optional<User> findUserById(UUID id);
}
