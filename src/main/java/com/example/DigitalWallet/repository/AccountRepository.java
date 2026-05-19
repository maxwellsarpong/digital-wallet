package com.example.DigitalWallet.repository;

import com.example.DigitalWallet.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String account);
    Optional<Account> findAccountById(UUID uuid);
    boolean existsByAccountNumber(String account);

}
