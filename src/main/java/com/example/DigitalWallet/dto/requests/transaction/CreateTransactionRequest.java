package com.example.DigitalWallet.dto.requests.transaction;

import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private String narration;
    private BigDecimal charges;
    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal destBalanceAfter;
    private BigDecimal destBalanceBefore;
    private String sourceAccount;
    private String destinationAccount;
    private Boolean reversed;
    private AccountType acctType;
}
