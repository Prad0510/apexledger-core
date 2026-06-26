package com.example.apexledger_core.service;

import com.example.apexledger_core.exception.InsufficientFundsException;
import com.example.apexledger_core.exception.RateLimitExceededException;
import com.example.apexledger_core.messaging.LedgerEventProducer;
import com.example.apexledger_core.model.Account;
import com.example.apexledger_core.model.Transaction;
import com.example.apexledger_core.repository.AccountRepository;
import com.example.apexledger_core.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LedgerEventProducer ledgerEventProducer;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void executeTransfer(String fromId, String toId, BigDecimal amount) {
        if (!rateLimiterService.isAllowed(fromId)) {
            throw new RateLimitExceededException("Rate limit exceeded for account: " + fromId + ". Request dropped.");
        }

        // Deadlock Avoidance Sequence Optimization
        String firstLockId = fromId.compareTo(toId)<0?fromId:toId;
        String secondLockId = firstLockId.equals(fromId)?toId:fromId;

        Account firstLockAccount = accountRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + firstLockId));
        Account secondLockAccount = accountRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + secondLockId));

        Account source = firstLockId.equals(fromId)?firstLockAccount:secondLockAccount;
        Account destination = firstLockId.equals(toId)?firstLockAccount:secondLockAccount;

        // Financial Invariant Validation
        if(source.getBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient funds in account:" + fromId);
        }

        // Update engine state
        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction successTx = new Transaction(fromId,toId,amount,"SUCCESS");
        transactionRepository.save(successTx);

        // Emit streaming event
        ledgerEventProducer.emitTransfer(fromId,toId,amount.toString());
    }
}
