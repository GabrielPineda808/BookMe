package com.example.book.exception;

public class ReviewUpdateException extends RuntimeException {
    public ReviewUpdateException(String message) {
        super(message);
    }
    
    public ReviewUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
