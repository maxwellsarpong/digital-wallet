package com.example.DigitalWallet.dto.requests.account;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAccountRequest {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal availableBalance;
    private String firstName;
    private String lastName;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
