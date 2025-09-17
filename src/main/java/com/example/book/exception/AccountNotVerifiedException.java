package com.example.book.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException(String message) {
        super(message);
    }
    
    public AccountNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}

