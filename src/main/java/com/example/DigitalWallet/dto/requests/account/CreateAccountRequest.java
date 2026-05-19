package com.example.DigitalWallet.dto.requests.account;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateAccountRequest {
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private BigDecimal availableBalance;
    @ManyToOne
    private User user;
}
