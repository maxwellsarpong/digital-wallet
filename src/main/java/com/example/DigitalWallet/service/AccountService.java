package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.requests.account.CreateAccountRequest;
import com.example.DigitalWallet.dto.requests.account.UpdateAccountRequest;
import com.example.DigitalWallet.dto.response.account.AccountResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.exception.AccountNumberException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.DigitalWallet.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private String generateAccount() {
        String prefix = "20";
        long randomPart = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return prefix + randomPart;
    }

    private AccountResponse mapAccountResponse(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .availableBalance(account.getAvailableBalance())
                .user(account.getUser())
                .build();
    }

    public AccountResponse addAccount(CreateAccountRequest request){
        String generatedAcct = generateAccount();

        if(accountRepository.existsByAccountNumber(generatedAcct)){
            throw new AccountNumberException("Account Number: " + generatedAcct + "already taken");
        }

        Account newAccount = Account.builder()
                .accountNumber(generatedAcct)
                .availableBalance(BigDecimal.ZERO)
                .accountType(request.getAccountType())
                .user(request.getUser())
                .build();

        Account savedAccount = accountRepository.save(newAccount);
        return mapAccountResponse(savedAccount);
    }

    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request){
        Account currentAccount = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account number not found")
        );
        if(request.getAccountType()!=null && !request.getAccountType().equals(currentAccount.getAccountType())){
            currentAccount.setAccountType(request.getAccountType());
        }
        currentAccount.setAvailableBalance(request.getAvailableBalance());
        currentAccount.setUpdatedAt(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(currentAccount);
        return mapAccountResponse(updatedAccount);
    }

    public AccountResponse getAnAccount(UUID id){
        Account acct = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account not found")
        );
        return mapAccountResponse(acct);
    }

    public void deleteAccount(UUID id){
        Account acct = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account not found")
        );
        accountRepository.delete(acct);
    }

    public List<AccountResponse> getAllAccounts(){
        return accountRepository.findAll().stream().map(this::mapAccountResponse).collect(Collectors.toList());
    }
}
