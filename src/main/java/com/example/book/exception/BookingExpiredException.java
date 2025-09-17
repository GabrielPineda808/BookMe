package com.example.book.exception;

public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException(String message) {
        super(message);
    }
    
    public BookingExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
