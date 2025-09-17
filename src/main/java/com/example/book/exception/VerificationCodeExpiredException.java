package com.example.book.exception;

public class VerificationCodeExpiredException extends RuntimeException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
    
    public VerificationCodeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

