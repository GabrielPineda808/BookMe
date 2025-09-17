package com.example.book.exception;

public class BookingOverlapException extends RuntimeException {
    public BookingOverlapException(String message) {
        super(message);
    }
    
    public BookingOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}

