package com.example.DigitalWallet.utils;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.entity.Wallet;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.mapper.MapTransactionResponse;
import com.example.DigitalWallet.repository.AccountRepository;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.repository.WalletRepository;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor

public class AccountUtil {
    private final WalletRepository walletRepository;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public static String generateAccount() {
        String prefix = "20";
        long randomPart = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return prefix + randomPart;
    }

    private void transferToWallet(CreateTransactionRequest request, Account sourceAcct, BigDecimal charges, BigDecimal totalDeduction){
        Wallet destWallet = walletRepository.findByWalletNumber(request.getDestinationAccount()).orElseThrow(
                ()-> new AccountNumberException("Account Number not found")
        );
        BigDecimal destCurrBal = destWallet.getBalance();
        BigDecimal destNewBal = destCurrBal.add(request.getAmount());

        BigDecimal sourceCurrBal = sourceAcct.getAvailableBalance();
        BigDecimal sourceNewBal = sourceCurrBal.subtract(totalDeduction);

        accountRepository.save(sourceAcct);
        walletRepository.save(destWallet);
        Transaction newTransaction = Transaction.builder()
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionReference(request.getTransactionReference())
                .charges(charges)
                .sourceAccount(request.getSourceAccount())
                .destinationAccount(request.getDestinationAccount())
                .acctType(AccountType.WALLET)
                .destBalanceAfter(destNewBal)
                .destBalanceBefore(destCurrBal)
                .sourceBalanceAfter(sourceCurrBal)
                .sourceBalanceBefore(sourceNewBal)
                .narration(request.getNarration())
                .reversed(false)
                .build();
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        MapTransactionResponse.mapTransactionResponse(savedTransaction);
    }

    public static BigDecimal generateCharges(BigDecimal amount){
        return amount.multiply(new BigDecimal("0.005"));
    }
}
