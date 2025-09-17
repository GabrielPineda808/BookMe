package com.example.book.exception;

public class ServiceNotOwnedByUserException extends RuntimeException {
    public ServiceNotOwnedByUserException(String message) {
        super(message);
    }

  public ServiceNotOwnedByUserException(String message, Throwable cause) {
    super(message, cause);
  }
}
