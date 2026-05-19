package com.example.DigitalWallet.controller;

import com.example.DigitalWallet.dto.requests.account.CreateAccountRequest;
import com.example.DigitalWallet.dto.requests.account.UpdateAccountRequest;
import com.example.DigitalWallet.dto.response.account.AccountResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.DigitalWallet.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AccountController {
    public final AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<AccountResponse> addAccount(@Valid @RequestBody CreateAccountRequest request){
        AccountResponse newAcct = accountService.addAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAcct);
    }

    @DeleteMapping("/account/{id}")
    public void deleteAccount(@PathVariable UUID id){
        accountService.deleteAccount(id);
    }

    @GetMapping("/account")
    public ResponseEntity<List<AccountResponse>> getAllAccounts(){
        List<AccountResponse> allAcct = accountService.getAllAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(allAcct);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountResponse> getSingleAccount(@PathVariable UUID id){
        AccountResponse acct = accountService.getAnAccount(id);
        return ResponseEntity.ok(acct);
    }

    @PutMapping("/account/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountRequest request){
        AccountResponse updateAcct = accountService.updateAccount(id, request);
        return ResponseEntity.ok(updateAcct);
    }
}
