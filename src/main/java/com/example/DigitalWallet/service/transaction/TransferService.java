package com.example.DigitalWallet.service.transaction;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.entity.Wallet;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.enums.TransactionType;
import com.example.DigitalWallet.event.TransactionEvent;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.exception.InsufficientFundsException;
import com.example.DigitalWallet.mapper.MapTransactionResponse;
import com.example.DigitalWallet.producer.TransactionProducer;
import com.example.DigitalWallet.repository.AccountRepository;
import com.example.DigitalWallet.repository.TransactionRepository;
import com.example.DigitalWallet.repository.WalletRepository;
import com.example.DigitalWallet.utils.AccountUtil;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

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

    private void transferToAccount(CreateTransactionRequest request, Account sourceAcct, BigDecimal charges, BigDecimal totalDeduction){
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
                .acctType(request.getAcctType())
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .transactionReference(request.getTransactionReference())
                .sourceBalanceBefore(sourceCurrBal)
                .sourceBalanceAfter(sourceNewBal)
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
        MapTransactionResponse.mapTransactionResponse(savedTransaction);
    }

    @Transactional
    public String transfer(CreateTransactionRequest request) throws InvalidTransactionException {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new InsufficientFundsException("Amount must be greater than 0");

        Account sourceAcct = accountRepository.findByAccountNumber(request.getSourceAccount())
                .orElseThrow(() -> new AccountNumberException(
                        "Source account not found: " + request.getSourceAccount()));

        BigDecimal charges = AccountUtil.generateCharges(request.getAmount());
        BigDecimal totalDeduction = charges.add(request.getAmount());

        if (sourceAcct.getAvailableBalance().compareTo(totalDeduction) <= 0)
            throw new InsufficientFundsException("Insufficient funds in account");

        boolean destExists = accountRepository.existsByAccountNumber(request.getDestinationAccount());
        if (!destExists && !request.getAcctType().equals(AccountType.WALLET))
            throw new AccountNumberException(
                    "Destination account not found: " + request.getDestinationAccount());

        if (!request.getTransactionType().equals(TransactionType.TRANSFER))
            throw new IllegalArgumentException("Invalid transaction type for transfer");

        TransactionEvent event = TransactionEvent.builder()
                .sourceAccount(request.getSourceAccount())
                .destinationAccount(request.getDestinationAccount())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .acctType(request.getAcctType())
                .narration(request.getNarration())
                .transactionReference(request.getTransactionReference())
                .build();

        transactionProducer.publishTransaction(event);
        log.info("Transaction submitted for processing: {}", request.getNarration());
        return "Transfer submitted for processing";
    }


    @Transactional
    public void processTransfer(CreateTransactionRequest request) throws InvalidTransactionException {
        if(request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new InsufficientFundsException("Amount should be greater than 0"+ request.getAmount());
        }

        boolean isAcctExist = accountRepository.existsByAccountNumber(request.getDestinationAccount());
        Account sourceAcct = accountRepository.findByAccountNumber(request.getSourceAccount()).orElseThrow(
                ()-> new AccountNumberException("Account " + request.getSourceAccount() +   "does not exist")
        );

        BigDecimal charges = AccountUtil.generateCharges(request.getAmount());
        BigDecimal totalDeduction = charges.add(request.getAmount());

        if(sourceAcct.getAvailableBalance().compareTo(totalDeduction) <= 0){
            throw new InsufficientFundsException("Insufficient funds in account");
        }

        if(request.getTransactionType().equals(TransactionType.TRANSFER) && isAcctExist && !Objects.equals(request.getAmount(), BigDecimal.ZERO)){
            if(request.getAcctType().equals(AccountType.CURRENT) || request.getAcctType().equals(AccountType.SAVINGS)){
                transferToAccount(request, sourceAcct, charges, totalDeduction);
                return;

            } else if (request.getAcctType().equals(AccountType.WALLET)) {
                transferToWallet(request, sourceAcct, charges, totalDeduction);
                return;
            }
        }
        AccountType accountType = request.getAcctType();
        throw new InvalidTransactionException("Unknown transaction" + accountType);
    }
}
