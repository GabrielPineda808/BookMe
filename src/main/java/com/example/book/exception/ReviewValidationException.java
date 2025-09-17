package com.example.book.exception;

public class ReviewValidationException extends RuntimeException {
    public ReviewValidationException(String message) {
        super(message);
    }
    
    public ReviewValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

