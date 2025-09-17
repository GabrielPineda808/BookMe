package com.example.book.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
    
    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

