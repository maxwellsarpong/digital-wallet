package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.requests.account.CreateAccountRequest;
import com.example.DigitalWallet.dto.requests.account.UpdateAccountRequest;
import com.example.DigitalWallet.dto.response.account.AccountResponse;
import com.example.DigitalWallet.entity.Account;
import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.AccountType;
import com.example.DigitalWallet.exception.AccountNumberException;
import com.example.DigitalWallet.exception.UserNotFoundException;
import com.example.DigitalWallet.mapper.MapAccountResponse;
import com.example.DigitalWallet.repository.UserRepository;
import com.example.DigitalWallet.utils.AccountUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @CacheEvict(value = "account", key = "'all'")
    public AccountResponse addAccount(CreateAccountRequest request){
        String generatedAcct = AccountUtil.generateAccount();
        int attempts = 0;

        while(accountRepository.existsByAccountNumber(generatedAcct)){
            if(++attempts >= 5){
                throw new AccountNumberException("Could not generate a unique account number");
            }
            generatedAcct = AccountUtil.generateAccount();
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
        return MapAccountResponse.mapAccountResponse(savedAccount);
    }

    @Caching(
            put    = { @CachePut(value = "account", key = "#id") },
            evict  = { @CacheEvict(value = "account", key = "'all'") }
    )
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
        return MapAccountResponse.mapAccountResponse(updatedAccount);
    }

    @Cacheable(value = "account", key = "#id")
    public AccountResponse getAnAccount(UUID id){
        Account acct = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account not found")
        );
        return MapAccountResponse.mapAccountResponse(acct);
    }

    @Caching(evict = {
            @CacheEvict(value = "account",   key = "#id"),
            @CacheEvict(value = "accounts",  key = "'all'")
    })
    public void deleteAccount(UUID id){
        Account acct = accountRepository.findAccountById(id).orElseThrow(
                ()-> new AccountNumberException("Account not found")
        );
        accountRepository.delete(acct);
    }

//    @Cacheable(value = true, key = "'all'")
    @Cacheable(value = "accounts", key = "'all'")
    public List<AccountResponse> getAllAccounts(){
        log.info("FETCHING FROM DATABASE");
        return accountRepository.findAll().stream().map(MapAccountResponse::mapAccountResponse).collect(Collectors.toList());
    }
}
