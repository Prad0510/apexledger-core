package com.example.apexledger_core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate; // Switch to string specialized template
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate; // Ensures pure string transmission

    // Your existing LUA_SCRIPT definition goes here...
    private static final String LUA_SCRIPT = "...your script code...";

    public boolean isAllowed(String accountId) {
        String bucketKey = "ratelimit:ledger:" + accountId;
        String maxCapacity = "5";
        String tokenPerSecond = "1";
        String currentEpochSecond = String.valueOf(System.currentTimeMillis() / 1000);

        List<String> keys = Collections.singletonList(bucketKey);

        try {
            // Using the native, error-free signature
            Long result = stringRedisTemplate.execute(
                    new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
                    keys,
                    maxCapacity, tokenPerSecond, currentEpochSecond
            );

            return result != null && result == 1;

        } catch (Exception e) {
            // Fail-open guard line: Log locally so network issues don't block the bank core
            System.err.println("⚠️ [REDIS SHIELD WARN] Rate limiter fallback activated: " + e.getMessage());
            return true;
        }
    }
}
