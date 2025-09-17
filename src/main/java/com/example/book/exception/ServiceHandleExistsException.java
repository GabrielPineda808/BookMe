package com.example.book.exception;

public class ServiceHandleExistsException extends RuntimeException {
    public ServiceHandleExistsException(String message) {
        super(message);
    }
    public ServiceHandleExistsException(String message, Throwable cause){super(message,cause);}
}
