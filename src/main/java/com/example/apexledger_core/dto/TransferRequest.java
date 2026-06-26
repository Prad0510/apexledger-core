package com.example.apexledger_core.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;

    public TransferRequest(){}

    public TransferRequest(String sourceAccountId,String destinationAccountId,BigDecimal amount){
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    public String getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getDestinationAccountId() { return sourceAccountId; }
    public void setDestinationAccountId(String sourceAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
