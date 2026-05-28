package com.example.DigitalWallet.service.transaction;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.exception.InsufficientFundsException;
import com.example.DigitalWallet.mapper.MapTransactionResponse;
import com.example.DigitalWallet.repository.AccountRepository;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.utils.AccountUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
@AllArgsConstructor
public class WithdrawService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private TransactionResponse withdrawMoney(CreateTransactionRequest request, Account srcAcct, BigDecimal amount, BigDecimal charges){

        BigDecimal sourceBal = srcAcct.getAvailableBalance();
        if (sourceBal.compareTo(request.getAmount()) <= 0) {
            log.error("Insufficient balance in account" + request.getSourceAccount());
            throw new InsufficientFundsException("Insufficient balance in account" + request.getSourceAccount());
        }
        BigDecimal currBal = sourceBal.subtract(request.getAmount());

        srcAcct.setAvailableBalance(currBal);
        accountRepository.save(srcAcct);


        try {
            Transaction newTransaction = Transaction.builder()
                    .transactionReference(request.getTransactionReference())
                    .transactionType(request.getTransactionType())
                    .transactionStatus(TransactionStatus.SUCCESS)
                    .destinationAccount("Not Applicable")
                    .sourceAccount(request.getSourceAccount())
                    .destBalanceAfter(BigDecimal.ZERO)
                    .destBalanceBefore(BigDecimal.ZERO)
                    .sourceBalanceAfter(currBal)
                    .sourceBalanceBefore(sourceBal)
                    .reversed(false)
                    .acctType(request.getAcctType())
                    .narration(request.getNarration())
                    .charges(charges)
                    .amount(amount)
                    .reversed(false)
                    .build();
            Transaction savedTransaction = transactionRepository.save(newTransaction);

            log.info("withdrawal transaction done: " + newTransaction.toString());
            return MapTransactionResponse.mapTransactionResponse(savedTransaction);

        }catch (Exception e){
            log.error("Error performing withdrawal transaction" + e.getMessage());
        }
        return null;
    }

    @Transactional
    public TransactionResponse processWithdraw(CreateTransactionRequest request){
        Account sourceAcct = accountRepository.findByAccountNumber(request.getSourceAccount()).orElseThrow(
                () -> {
                    log.error("Source Account not found: " + request.getSourceAccount());
                    return new AccountNumberException("Source Account not found: " + request.getSourceAccount());
                });
        BigDecimal charges = AccountUtil.generateCharges(request.getAmount());

        if(request.getTransactionType() != null && request.getTransactionType().equals(TransactionType.WITHDRAWAL)){
            return withdrawMoney(request, sourceAcct, request.getAmount(), charges);
        }
        return null;
    }
}
