package com.example.DigitalWallet.event;

import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private TransactionType transactionType;
    private AccountType acctType;
    private String narration;
    private String transactionReference;
}