package com.example.book.exception;

public class BookingTimeValidationException extends RuntimeException {
    public BookingTimeValidationException(String message) {
        super(message);
    }
    
    public BookingTimeValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
