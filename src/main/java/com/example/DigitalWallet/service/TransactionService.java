package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.entity.Wallet;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.exception.InsufficientFundsException;
import com.example.DigitalWallet.exception.TransactionNotFoundException;
import com.example.DigitalWallet.repository.AccountRepository;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.repository.WalletRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private WalletRepository walletRepository;

    private BigDecimal generateCharges(BigDecimal amount){
        return amount.multiply(new BigDecimal("0.005"));
    }

    private TransactionResponse transferToWallet(CreateTransactionRequest request, Account sourceAcct, BigDecimal charges, BigDecimal totalDeduction){
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
        return mapTransactionResponse(savedTransaction);
    }

    private TransactionResponse transferToAccount(CreateTransactionRequest request, Account sourceAcct, BigDecimal charges, BigDecimal totalDeduction){
        BigDecimal sourceCurrBal = sourceAcct.getAvailableBalance();
        BigDecimal sourceNewBal = sourceCurrBal.subtract(totalDeduction);

        Account destAcct = accountRepository.findByAccountNumber(request.getDestinationAccount()).orElseThrow(
                ()-> new AccountNumberException("Account not found")
        );
        BigDecimal destCurrBal = destAcct.getAvailableBalance();
        BigDecimal destNewBal = destAcct.getAvailableBalance().add(request.getAmount());
        destAcct.setAvailableBalance(destNewBal);
        sourceAcct.setAvailableBalance(sourceNewBal);

        accountRepository.save(destAcct);
        accountRepository.save(sourceAcct);

        Transaction newTransaction = Transaction.builder()
                .acctType(AccountType.CURRENT)
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .transactionReference(request.getTransactionReference())
                .sourceBalanceAfter(sourceNewBal)
                .sourceBalanceBefore(sourceCurrBal)
                .destBalanceAfter(destNewBal)
                .destBalanceBefore(destCurrBal)
                .transactionStatus(TransactionStatus.SUCCESS)
                .narration(request.getNarration())
                .sourceAccount(request.getSourceAccount())
                .reversed(false)
                .destinationAccount(request.getDestinationAccount())
                .charges(charges)
                .build();
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return mapTransactionResponse(savedTransaction);
    }

    private TransactionResponse mapTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .acctType(transaction.getAcctType())
                .amount(transaction.getAmount())
                .destBalanceAfter(transaction.getDestBalanceAfter())
                .destBalanceBefore(transaction.getDestBalanceAfter())
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

    @Transactional
    public TransactionResponse transfer(CreateTransactionRequest request) throws InvalidTransactionException {
        if(request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new InsufficientFundsException("Amount should be greater than 0"+ request.getAmount());
        }

        boolean isAcctExist = accountRepository.existsByAccountNumber(request.getDestinationAccount());
        //verify the source account for the new deposit has money
        Account sourceAcct = accountRepository.findByAccountNumber(request.getSourceAccount()).orElseThrow(
                ()-> new AccountNumberException("Account " + request.getSourceAccount() +   "does not exist")
        );


        BigDecimal charges = generateCharges(request.getAmount());
        BigDecimal totalDeduction = charges.add(request.getAmount());

        if(sourceAcct.getAvailableBalance().compareTo(totalDeduction) <= 0){
            throw new InsufficientFundsException("Insufficient funds in account");
        }

        if(request.getTransactionType().equals(TransactionType.TRANSFER) && isAcctExist && !Objects.equals(request.getAmount(), BigDecimal.ZERO)){
            if(request.getAcctType().equals(AccountType.CURRENT) || request.getAcctType().equals(AccountType.SAVINGS)){
                return transferToAccount(request, sourceAcct, charges, totalDeduction);

            } else if (request.getAcctType().equals(AccountType.WALLET)) {
                return transferToWallet(request, sourceAcct, charges, totalDeduction);
            }
        }
        AccountType accountType = request.getAcctType();
        throw new InvalidTransactionException("Unknown transaction" + accountType);
    }

    @Transactional
    public void withdraw(){
    }

    @Transactional
    public void deposit(){}

    @Transactional
    public List<TransactionResponse>getAllTransactions(){
        return transactionRepository.findAll().stream().map(this::mapTransactionResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse getASingleTransaction(@PathVariable UUID id){
        Transaction currTransact = transactionRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Request transaction id" + id + "does not exists");
                    return new TransactionNotFoundException("Request transaction id" + id + "does not exists");
                });
        return mapTransactionResponse(currTransact);

    }
}
