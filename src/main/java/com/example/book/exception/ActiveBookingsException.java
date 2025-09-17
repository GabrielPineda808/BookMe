package com.example.book.exception;

public class ActiveBookingsException extends RuntimeException {
    public ActiveBookingsException(String message) {
        super(message);
    }
  public ActiveBookingsException(String message, Throwable cause) {
    super(message, cause);
  }
}
