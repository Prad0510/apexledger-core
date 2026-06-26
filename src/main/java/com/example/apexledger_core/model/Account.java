package com.example.apexledger_core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_id", length = 50)
    private String accountId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "balance", nullable = false, precision = 15, scale = 4)
    BigDecimal balance = new BigDecimal("0.0000");

    @Column(name = "currency", length = 3, nullable = false)
    String currency = "INR";

    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    public Account(){}

    public Account(String accountId, String userId, BigDecimal balance){
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public String getAccountId(){
        return accountId;
    }

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

    public String getCurrency(){
        return currency;
    }

    public void setCurrency(String currency){
        this.currency = currency;
    }

    public ZonedDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt){
        this.createdAt = createdAt;
    }
}
