package com.example.DigitalWallet.controller;

import com.example.DigitalWallet.dto.requests.wallet.CreateWalletRequest;
import com.example.DigitalWallet.dto.requests.wallet.UpdateWalletRequest;
import com.example.DigitalWallet.dto.response.wallet.WalletResponse;
import com.example.DigitalWallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping()
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request){
        WalletResponse newWallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(newWallet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> updateWallet(@PathVariable UUID id, @RequestBody UpdateWalletRequest request){
        WalletResponse currWallet = walletService.updateWallet(id, request);
        return ResponseEntity.ok(currWallet);
    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable UUID id){
        walletService.deleteWalletById(id);
    }

    @GetMapping()
    public ResponseEntity<List<WalletResponse>> getAllWallet(){
        List<WalletResponse> allWallets = walletService.getAllWallet();
        return ResponseEntity.ok(allWallets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getAWallet(@PathVariable UUID id){
        WalletResponse walletResponse = walletService.getASingleWallet(id);
        return ResponseEntity.ok(walletResponse);
    }
}
