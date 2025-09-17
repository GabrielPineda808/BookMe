package com.example.book.exception;

public class BookingChangeRequestStatusException extends RuntimeException {
    public BookingChangeRequestStatusException(String message) {
        super(message);
    }
    
    public BookingChangeRequestStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
