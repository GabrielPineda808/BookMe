package com.example.book.exception;

public class ReviewOwnershipException extends RuntimeException {
    public ReviewOwnershipException(String message) {
        super(message);
    }
    
    public ReviewOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
