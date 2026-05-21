package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.requests.account.CreateAccountRequest;
import com.example.DigitalWallet.dto.requests.account.UpdateAccountRequest;
import com.example.DigitalWallet.dto.response.account.AccountResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.exception.UserNotFoundException;
import com.example.DigitalWallet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.DigitalWallet.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private String generateAccount() {
        String prefix = "20";
        long randomPart = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return prefix + randomPart;
    }

    private AccountResponse mapAccountResponse(Account account){
        if (account.getUser() == null) {
            throw new IllegalStateException("Account has no associated user: " + account.getId());
        }
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .availableBalance(account.getAvailableBalance())
                .userId(account.getUser().getId())
                .firstName(account.getUser().getFirstName())
                .lastName(account.getUser().getLastName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public AccountResponse addAccount(CreateAccountRequest request){
        String generatedAcct = generateAccount();
        int attempts = 0;

        while(accountRepository.existsByAccountNumber(generatedAcct)){
            if(++attempts >= 5){
                throw new AccountNumberException("Could not generate a unique account number");
            }
            generatedAcct = generateAccount();
        }

        User userRel = userRepository.findUserById(request.getUserId()).orElseThrow(
                ()->{log.error("User: " + request.getUserId() + "not found");
                    return new UserNotFoundException("User: " + request.getUserId() + "not found");
                });
        if(accountRepository.existsByAccountNumber(generatedAcct)){
            throw new AccountNumberException("Account Number: " + generatedAcct + "already taken");
        }

        Account newAccount = Account.builder()
                .accountNumber(generatedAcct)
                .availableBalance(BigDecimal.ZERO)
                .accountType(request.getAccountType())
                .user(userRel)
                .build();

        userRel.addAccount(newAccount);
        Account savedAccount = accountRepository.save(newAccount);
        return mapAccountResponse(savedAccount);
    }

    public AccountResponse updateAccount(UUID id, UpdateAccountRequest request){
        Account currentAccount = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account number not found")
        );

        User userRel = userRepository.findUserById(request.getUserId()).orElseThrow(
                ()-> new UserNotFoundException("User: " + request.getUserId() + "not found"));

        if(request.getAccountType()!=null && !request.getAccountType().equals(currentAccount.getAccountType())){
            currentAccount.setAccountType(request.getAccountType());
        }
        if(request.getAvailableBalance() != null) {
            currentAccount.setAvailableBalance(request.getAvailableBalance());
        }
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
