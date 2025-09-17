package com.example.book.exception;

public class ServiceIntervalException extends RuntimeException {
    public ServiceIntervalException(String message) {
        super(message);
    }
    
    public ServiceIntervalException(String message, Throwable cause) {
        super(message, cause);
    }
}
