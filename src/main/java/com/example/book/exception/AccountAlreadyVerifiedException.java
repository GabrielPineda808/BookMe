package com.example.book.exception;

public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
    
    public AccountAlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}

