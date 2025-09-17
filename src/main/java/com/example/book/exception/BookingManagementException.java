package com.example.book.exception;

public class BookingManagementException extends RuntimeException {
    public BookingManagementException(String message) {
        super(message);
    }
    
    public BookingManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

