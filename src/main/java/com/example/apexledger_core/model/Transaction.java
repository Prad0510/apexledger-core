package com.example.apexledger_core.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id",updatable = false,nullable = false)
    private UUID transactionId;

    @Column(name = "source_account_id",length = 50, updatable = false,nullable = false)
    private String sourceAccountId;

    @Column(name = "destination_account_id",length = 50, updatable = false,nullable = false)
    private String destinationAccountId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    public Transaction(){}

    public Transaction(String sourceAccountId, String destinationAccountId, BigDecimal amount, String status){
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
    public String getSourceAccountId() {
        return sourceAccountId;
    }
    public String getDestinationAccountId() {
        return destinationAccountId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getFailureReason() {
        return failureReason;
    }
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
