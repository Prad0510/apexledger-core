package com.example.apexledger_core.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LedgerEventProducer {

    private static final String TOPIC = "ledger-transactions";

    @Autowired(required = false)
    private KafkaTemplate<String,String> kafkaTemplate;

    public void emitTransfer(String sourceAccountId, String destinationAccountId, String amount) {
        String eventPayload = String.format(
                "{\"event\":\"TRANSFER_EXECUTED\",\"source\":\"%s\",\"destination\":\"%s\",\"amount\":\"%s\"}",
                sourceAccountId,destinationAccountId,amount
        );

        try {
            if (kafkaTemplate != null) {
                kafkaTemplate.send(TOPIC,sourceAccountId,eventPayload);
                System.out.println("[KAFKA PRODUCER] Emitted transaction event to topic '"
                        + TOPIC + "':" + eventPayload);
            } else {
                System.out.println("[KAFKA PRODUCER WARN] KafkaTemplate not initialized. Logging payload locally: " + eventPayload);
            }
        } catch (Exception e){
            System.err.println("[KAFKA ERROR] Failed to stream event onto broker lines: " + e.getMessage());
        }

    }
}
