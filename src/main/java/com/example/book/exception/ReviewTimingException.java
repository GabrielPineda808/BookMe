package com.example.book.exception;

public class ReviewTimingException extends RuntimeException {
    public ReviewTimingException(String message) {
        super(message);
    }
    
    public ReviewTimingException(String message, Throwable cause) {
        super(message, cause);
    }
}
