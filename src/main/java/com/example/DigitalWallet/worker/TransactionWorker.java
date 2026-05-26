package com.example.DigitalWallet.worker;

import com.example.DigitalWallet.config.RabbitMQConfig;
import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.event.TransactionEvent;
import com.example.DigitalWallet.service.TransactionService;
import jakarta.transaction.InvalidTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionWorker {

    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE)
    public void processTransaction(TransactionEvent event) {
        log.info("Received transaction from queue: {}", event.getTransactionReference());
        try {
            CreateTransactionRequest request = CreateTransactionRequest.builder()
                    .sourceAccount(event.getSourceAccount())
                    .destinationAccount(event.getDestinationAccount())
                    .amount(event.getAmount())
                    .transactionType(event.getTransactionType())
                    .acctType(event.getAcctType())
                    .narration(event.getNarration())
                    .transactionReference(event.getTransactionReference())
                    .build();

            transactionService.processTransfer(request);
            log.info("Transaction processed successfully: {}", event.getTransactionReference());

        } catch (InvalidTransactionException e) {
            log.error("Transaction failed — invalid type: {} — {}",
                    event.getTransactionReference(), e.getMessage());
            throw new RuntimeException("Transaction processing failed", e);
        } catch (Exception e) {
            log.error("Transaction failed: {} — {}",
                    event.getTransactionReference(), e.getMessage());
            throw new RuntimeException("Transaction processing failed", e);
        }
    }
}