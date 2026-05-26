package com.example.DigitalWallet.producer;
import com.example.DigitalWallet.config.RabbitMQConfig;
import com.example.DigitalWallet.dto.requests.transaction.CreateTransactionRequest;
import com.example.DigitalWallet.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishTransaction(TransactionEvent event) {

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TRANSACTION_EXCHANGE,
                    RabbitMQConfig.TRANSACTION_ROUTING_KEY,
                    event
            );
            log.info("Transaction event published: {}", event.getNarration());
        } catch (Exception e) {
            log.error("Failed to publish transaction event: {}", e.getMessage());
            throw new RuntimeException("Failed to publish transaction event", e);
        }
    }
}