package com.example.apexledger_core.exception;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message) {
        super(message);
    }
}
