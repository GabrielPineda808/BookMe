package com.example.book.exception;

public class BookingDurationException extends RuntimeException {
    public BookingDurationException(String message) {
        super(message);
    }
    
    public BookingDurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
