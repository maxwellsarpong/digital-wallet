package com.example.DigitalWallet.mapper;

import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.entity.Transaction;
import lombok.AllArgsConstructor;

public class MapTransactionResponse {

    public static TransactionResponse mapTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .acctType(transaction.getAcctType())
                .amount(transaction.getAmount())
                .destBalanceAfter(transaction.getDestBalanceAfter())
                .destBalanceBefore(transaction.getDestBalanceBefore())
                .sourceBalanceAfter(transaction.getSourceBalanceAfter())
                .sourceBalanceBefore(transaction.getSourceBalanceBefore())
                .transactionReference(transaction.getTransactionReference())
                .transactionStatus(transaction.getTransactionStatus())
                .transactionType(transaction.getTransactionType())
                .reversed(transaction.getReversed())
                .charges(transaction.getCharges())
                .destinationAccount(transaction.getDestinationAccount())
                .createdAt(transaction.getCreatedAt())
                .sourceAccount(transaction.getSourceAccount())
                .narration(transaction.getNarration())
                .build();
    }
}
