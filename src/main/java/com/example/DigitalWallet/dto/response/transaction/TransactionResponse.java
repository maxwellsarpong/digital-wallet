package com.example.DigitalWallet.dto.response.transaction;

import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private String narration;
    private BigDecimal charges;
    private BigDecimal destBalanceBefore;
    private BigDecimal destBalanceAfter;
    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private String sourceAccount;
    private String destinationAccount;
    private Boolean reversed;
    private AccountType acctType;
    private LocalDateTime createdAt;

}
