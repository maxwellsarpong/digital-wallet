package com.example.DigitalWallet.dto.response.account;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private BigDecimal availableBalance;
    private User user;
}
