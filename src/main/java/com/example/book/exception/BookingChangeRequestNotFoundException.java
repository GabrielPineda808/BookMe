package com.example.book.exception;

public class BookingChangeRequestNotFoundException extends RuntimeException {
    public BookingChangeRequestNotFoundException(String message) {
        super(message);
    }
    
    public BookingChangeRequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
