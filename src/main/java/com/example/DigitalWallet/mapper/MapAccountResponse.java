package com.example.DigitalWallet.mapper;

import com.example.DigitalWallet.dto.response.account.AccountResponse;
import com.example.DigitalWallet.entity.Account;

public class MapAccountResponse {

    public static AccountResponse mapAccountResponse(Account account){
        if (account.getUser() == null) {
            throw new IllegalStateException("Account has no associated user: " + account.getId());
        }
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .availableBalance(account.getAvailableBalance())
                .userId(account.getUser().getId())
                .firstName(account.getUser().getFirstName())
                .lastName(account.getUser().getLastName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
