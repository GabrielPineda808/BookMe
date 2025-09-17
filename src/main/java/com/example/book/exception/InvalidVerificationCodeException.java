package com.example.book.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
    
    public InvalidVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}

