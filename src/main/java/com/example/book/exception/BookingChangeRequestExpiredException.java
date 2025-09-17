package com.example.book.exception;

public class BookingChangeRequestExpiredException extends RuntimeException {
    public BookingChangeRequestExpiredException(String message) {
        super(message);
    }
    
    public BookingChangeRequestExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
