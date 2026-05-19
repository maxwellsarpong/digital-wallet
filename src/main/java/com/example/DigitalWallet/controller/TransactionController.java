package com.example.DigitalWallet.controller;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.service.TransactionService;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransferTransaction(@Valid @RequestBody  CreateTransactionRequest request) throws InvalidTransactionException {
        TransactionResponse newTransaction = transactionService.transfer(request);
        return ResponseEntity.ok(newTransaction);
    }

    @PostMapping("/deposit")
    public void createDepositTransaction(@Valid @RequestBody CreateTransactionRequest request){

    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> createWithdrawTransaction(@Valid @RequestBody CreateTransactionRequest request){
        TransactionResponse withDrawal = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(withDrawal);
    }

    @GetMapping()
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        List<TransactionResponse> allTransactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(allTransactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getASingleTransaction(@PathVariable UUID id){
        TransactionResponse transactionResponse = transactionService.getASingleTransaction(id);
        return ResponseEntity.ok(transactionResponse);
    }
}
