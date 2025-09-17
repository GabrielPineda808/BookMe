package com.example.book.exception;

public class BookingChangeRequestOwnershipException extends RuntimeException {
    public BookingChangeRequestOwnershipException(String message) {
        super(message);
    }
    
    public BookingChangeRequestOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
