package com.example.DigitalWallet.dto.requests.account;

import com.example.DigitalWallet.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal availableBalance;
    private UUID userId;
}
