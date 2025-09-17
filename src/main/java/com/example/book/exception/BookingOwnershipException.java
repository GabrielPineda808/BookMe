package com.example.book.exception;

public class BookingOwnershipException extends RuntimeException {
    public BookingOwnershipException(String message) {
        super(message);
    }
    
    public BookingOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
