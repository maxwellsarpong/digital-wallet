package com.example.DigitalWallet.dto.requests.wallet;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.WalletStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class UpdateWalletRequest {
    private BigDecimal balance;

    private boolean locked;

    private WalletStatus status;

    private User user;
}
