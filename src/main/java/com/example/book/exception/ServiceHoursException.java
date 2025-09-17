package com.example.book.exception;

public class ServiceHoursException extends RuntimeException {
    public ServiceHoursException(String message) {
        super(message);
    }
    
    public ServiceHoursException(String message, Throwable cause) {
        super(message, cause);
    }
}
