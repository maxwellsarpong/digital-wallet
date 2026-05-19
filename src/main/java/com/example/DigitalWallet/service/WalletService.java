package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.requests.wallet.CreateWalletRequest;
import com.example.DigitalWallet.dto.requests.wallet.UpdateWalletRequest;
import com.example.DigitalWallet.dto.response.wallet.WalletResponse;
import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.entity.Wallet;
import com.example.DigitalWallet.enums.WalletStatus;
import com.example.DigitalWallet.exception.UserNotFoundException;
import com.example.DigitalWallet.exception.WalletFoundException;
import com.example.DigitalWallet.exception.WalletNotFoundException;
import com.example.DigitalWallet.repository.UserRepository;
import com.example.DigitalWallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WalletService {
    public final WalletRepository walletRepository;
    public final UserRepository userRepository;

    private WalletResponse mapWalletResponse(Wallet request){
        return WalletResponse.builder()
                .id(request.getId())
                .walletNumber(request.getWalletNumber())
                .balance(request.getBalance())
                .status(request.getStatus())
                .locked(false)
                .userID(request.getUser().getId())
                .build();
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request){
        if(request.getWalletNumber().equals(walletRepository.findByWalletNumber(request.getWalletNumber()))){
            log.error("wallet number: " + request.getWalletNumber() + "already exists");
            throw new WalletFoundException("Wallet Number: " + request.getWalletNumber() + "already exist");
        }
        if(request.getWalletNumber().isBlank()){
            log.error("Wallet request is empty");
            throw new WalletNotFoundException("Wallet request is empty");
        }

        User user = userRepository.findUserById(request.getUserId()).orElseThrow(
                ()-> new UserNotFoundException("User not found"));

        Wallet newWallet = Wallet.builder()
                .walletNumber(request.getWalletNumber())
                .user(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .locked(false)
                .build();

        log.info("Wallet: " + newWallet.getWalletNumber() + "Created");
        Wallet savedWallet = walletRepository.save(newWallet);
        return mapWalletResponse(savedWallet);

    }

    @Transactional()
    public List<WalletResponse> getAllWallet(){
        return walletRepository.findAll().stream().map(this::mapWalletResponse).collect(Collectors.toList());
    }

    @Transactional()
    public WalletResponse getASingleWallet(UUID id){
        Wallet currWallet = walletRepository.findById(id).orElseThrow(
                ()-> new WalletNotFoundException("Wallet not found")
        );
        return mapWalletResponse(currWallet);
    }

    public WalletResponse updateWallet(UUID id, UpdateWalletRequest request){
        Wallet currWallet = walletRepository.findById(id).orElseThrow(
                ()-> new WalletNotFoundException("Wallet not found")
        );
        if(request.getBalance() != null){
            currWallet.setBalance(request.getBalance());
        }
        if(!request.isLocked()){
            currWallet.setLocked(false);
        }
        if(request.getUser() != null){
            currWallet.setUser(request.getUser());
        }
        if(request.getStatus()!=null){
            currWallet.setStatus(request.getStatus());
        }
        Wallet newSavedWallet = walletRepository.save(currWallet);
        return mapWalletResponse(newSavedWallet);
    }

    public void deleteWalletById(UUID id){
        Wallet currWallet = walletRepository.findById(id).orElseThrow(
                ()-> new WalletNotFoundException("Wallet not found")
        );
        walletRepository.delete(currWallet);
    }
}
