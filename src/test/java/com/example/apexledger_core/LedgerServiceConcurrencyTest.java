package com.example.apexledger_core;

import com.example.apexledger_core.model.Account;
import com.example.apexledger_core.repository.AccountRepository;
import com.example.apexledger_core.repository.TransactionRepository;
import com.example.apexledger_core.service.LedgerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LedgerServiceConcurrencyTest {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setupTransactions(){
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        accountRepository.save(new Account("ACC-111","USR-PRADNYA",new BigDecimal("1000.0000")));
        accountRepository.save(new Account("ACC-222","USR-SAM",new BigDecimal("0.0000")));
    }

    @Test
    public void testConcurrentTransfers_ShouldMaintainDataIntegrity() throws InterruptedException{
        int totalRequests = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(totalRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(totalRequests);

        for(int i=0;i<totalRequests;i++){
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    ;
                    ledgerService.executeTransfer("ACC-111", "ACC-222", new BigDecimal("150.0000"));
                } catch (Exception ignored) {

                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();

        Account sourceResult = accountRepository.findById("ACC-111").orElseThrow();
        Account destinationResult = accountRepository.findById("ACC-222").orElseThrow();

        System.out.println("--- CONCURRENCY RUN METRICS ---");
        System.out.println("Final Balance Source (A): " + sourceResult.getBalance());
        System.out.println("Final Balance Destination (B): " + destinationResult.getBalance());
        System.out.println("Total Ledger Records Saved: " + transactionRepository.count());

        assertEquals(0,new BigDecimal("100.0000").compareTo(sourceResult.getBalance()));
        assertEquals(0, new BigDecimal("900.0000").compareTo(destinationResult.getBalance()));
    }
}
