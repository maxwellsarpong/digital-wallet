package com.example.DigitalWallet.service.transaction;

import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.enums.TransactionStatus;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.mapper.MapTransactionResponse;
import com.example.DigitalWallet.repository.AccountRepository;
import com.example.DigitalWallet.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class DepositService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(CreateTransactionRequest request){
        Account currAcct = accountRepository.findByAccountNumber(request.getDestinationAccount()).orElseThrow(
                ()->{
                    log.error("Destination account number not found: " + request.getDestinationAccount());
                    return new AccountNumberException("Destination account number not found: " + request.getDestinationAccount());
                }
        );
        if(request.getDestinationAccount()!=null || accountRepository.existsByAccountNumber(currAcct.getAccountNumber())){
            if(request.getAmount().compareTo(BigDecimal.ZERO) > 0){

                BigDecimal currBal = currAcct.getAvailableBalance();
                BigDecimal newBal = currBal.add(request.getAmount());
                try{
                    Transaction newTransaction = Transaction.builder()
                            .amount(request.getAmount())
                            .transactionStatus(TransactionStatus.SUCCESS)
                            .charges(BigDecimal.ZERO)
                            .narration(request.getNarration())
                            .transactionType(request.getTransactionType())
                            .transactionReference(request.getTransactionReference())
                            .acctType(request.getAcctType())
                            .destinationAccount(request.getDestinationAccount())
                            .sourceAccount("Not applicable")
                            .sourceBalanceAfter(BigDecimal.ZERO)
                            .sourceBalanceBefore(BigDecimal.ZERO)
                            .reversed(false)
                            .sourceBalanceAfter(BigDecimal.ZERO)
                            .destBalanceBefore(currBal)
                            .destBalanceAfter(newBal)
                            .build();

                    currAcct.setAvailableBalance(newBal);
                    accountRepository.save(currAcct);

                    Transaction savedTransaction = transactionRepository.save(newTransaction);
                    return MapTransactionResponse.mapTransactionResponse(savedTransaction);
                }catch (Exception e){
                    log.error("Error creating Deposit transaction" + e.getMessage());
                }
            }

        }
        return null;
    }
}
