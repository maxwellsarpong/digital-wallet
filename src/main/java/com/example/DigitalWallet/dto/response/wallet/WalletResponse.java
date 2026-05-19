package com.example.DigitalWallet.dto.response.wallet;

import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.WalletStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse implements Serializable {
    private UUID id;

    private String walletNumber;

    private BigDecimal balance;

    private boolean locked;

    private WalletStatus status;

    private UUID userID;
    private LocalDateTime createdAt;

}
