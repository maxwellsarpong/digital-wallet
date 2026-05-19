package com.example.DigitalWallet.entity;

import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;

    @Column(nullable = false)
    private BigDecimal amount;

    private String narration;

    @Column(nullable = false)
    private BigDecimal charges;

    @Column(nullable = false)
    private BigDecimal sourceBalanceBefore;

    @Column(nullable = false)
    private BigDecimal sourceBalanceAfter;

    @Column(nullable = false)
    private BigDecimal destBalanceBefore;

    @Column(nullable = false)
    private BigDecimal destBalanceAfter;

    @Column(nullable = false)
    private String sourceAccount;

    @Column(nullable = false)
    private String destinationAccount;

    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType acctType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean reversed = false;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.transactionDate = LocalDateTime.now();
    }

}
