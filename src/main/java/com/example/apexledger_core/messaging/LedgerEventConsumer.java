package com.example.apexledger_core.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LedgerEventConsumer {

    @KafkaListener(topics = "ledger-transactions",groupId = "apex-ledger-group")
    public void consumeTransactionEvent(String message){
        System.out.println("[KAFKA CONSUMER] Intercepted live event from stream!");
        System.out.println("Raw Payload Data: " + message);
        System.out.println("Background Task Status: Complete.\n");
    }
}
