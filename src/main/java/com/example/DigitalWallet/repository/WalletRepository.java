package com.example.DigitalWallet.repository;

import com.example.DigitalWallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByWalletNumber(String walletNumber);
    Optional<Wallet> findById(UUID id);
}
