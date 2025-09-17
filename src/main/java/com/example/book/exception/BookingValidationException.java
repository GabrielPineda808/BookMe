package com.example.book.exception;

public class BookingValidationException extends RuntimeException {
    public BookingValidationException(String message) {
        super(message);
    }
    
    public BookingValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

