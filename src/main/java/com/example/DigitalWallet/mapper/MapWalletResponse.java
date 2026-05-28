package com.example.DigitalWallet.mapper;

import com.example.DigitalWallet.dto.response.wallet.WalletResponse;
import com.example.DigitalWallet.entity.Wallet;

public class MapWalletResponse {

    public static WalletResponse mapWalletResponse(Wallet request){
        return WalletResponse.builder()
                .id(request.getId())
                .walletNumber(request.getWalletNumber())
                .balance(request.getBalance())
                .status(request.getStatus())
                .locked(false)
                .userID(request.getUser().getId())
                .build();
    }
}
