package com.example.DigitalWallet.controller;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.service.TransactionService;
import com.example.DigitalWallet.service.transaction.DepositService;
import com.example.DigitalWallet.service.transaction.TransferService;
import com.example.DigitalWallet.service.transaction.WithdrawService;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final DepositService depositService;
    private final TransferService transferService;
    private final WithdrawService withdrawService;

    @PostMapping("/transfer")
    public ResponseEntity<String> createTransferTransaction(@Valid @RequestBody  CreateTransactionRequest request) throws InvalidTransactionException {
        return ResponseEntity.status(HttpStatus.CREATED).body((transferService.transfer(request)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> createDepositTransaction(@Valid @RequestBody CreateTransactionRequest request){
        TransactionResponse transactionResponse = depositService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);

    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> createWithdrawTransaction(@Valid @RequestBody CreateTransactionRequest request){
        TransactionResponse withDraw = withdrawService.processWithdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(withDraw);
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
