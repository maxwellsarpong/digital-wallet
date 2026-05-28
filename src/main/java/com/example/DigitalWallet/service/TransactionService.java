package com.example.DigitalWallet.service;

import com.example.DigitalWallet.dto.response.transaction.TransactionResponse;
import com.example.DigitalWallet.entity.Transaction;
import com.example.DigitalWallet.exception.TransactionNotFoundException;
import com.example.DigitalWallet.mapper.MapTransactionResponse;
import com.example.DigitalWallet.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "transactions", key = "'all'")
    @Transactional
    public List<TransactionResponse>getAllTransactions(){
        return transactionRepository.findAll().stream().map(MapTransactionResponse::mapTransactionResponse).collect(Collectors.toList());
    }

    @Cacheable(value = "transactions", key = "#id")
    @Transactional
    public TransactionResponse getASingleTransaction(@PathVariable UUID id){
        Transaction currTransact = transactionRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Request transaction id" + id + "does not exists");
                    return new TransactionNotFoundException("Request transaction id" + id + "does not exists");
                });
        return MapTransactionResponse.mapTransactionResponse(currTransact);

    }
}
