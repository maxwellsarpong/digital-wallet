package com.example.DigitalWallet.dto.requests.wallet;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.WalletStatus;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {
    @NotNull(message = "Wallet number required")
    private String walletNumber;

    @NotNull(message="User id is required")
    private UUID userId;
}
