package com.example.book.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Custom Business Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        logger.warn("User not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotFoundException(
            ServiceNotFoundException ex, HttpServletRequest request) {
        logger.warn("Service not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "SERVICE_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(
            BookingNotFoundException ex, HttpServletRequest request) {
        logger.warn("Booking not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BOOKING_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFoundException(
            ReviewNotFoundException ex, HttpServletRequest request) {
        logger.warn("Review not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "REVIEW_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Validation Exceptions
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ErrorResponse> handleBookingValidationException(
            BookingValidationException ex, HttpServletRequest request) {
        logger.warn("Booking validation failed: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BOOKING_VALIDATION_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BookingOverlapException.class)
    public ResponseEntity<ErrorResponse> handleBookingOverlapException(
            BookingOverlapException ex, HttpServletRequest request) {
        logger.warn("Booking overlap detected: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BOOKING_OVERLAP_ERROR",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BookingManagementException.class)
    public ResponseEntity<ErrorResponse> handleBookingManagementException(
            BookingManagementException ex, HttpServletRequest request) {
        logger.warn("Booking management error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BOOKING_MANAGEMENT_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ReviewValidationException.class)
    public ResponseEntity<ErrorResponse> handleReviewValidationException(
            ReviewValidationException ex, HttpServletRequest request) {
        logger.warn("Review validation failed: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "REVIEW_VALIDATION_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ServiceHandleExistsException.class)
    public ResponseEntity<ErrorResponse> handleServiceHandleExistsException(
            ServiceHandleExistsException ex, HttpServletRequest request) {
        logger.warn("Service handle exists: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "SERVICE_HANDLE_EXISTS",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ServiceNotOwnedByUserException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotOwnedByUserException(
            ServiceNotOwnedByUserException ex, HttpServletRequest request) {
        logger.warn("Service not owned by user: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "SERVICE_NOT_OWNED_BY_USER",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ActiveBookingsException.class)
    public ResponseEntity<ErrorResponse> handleActiveBookingsException(
            ActiveBookingsException ex, HttpServletRequest request) {
        logger.warn("Service has active bookings: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ACTIVE_BOOKINGS_FOUND",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Authentication/Authorization Exceptions
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotVerifiedException(
            AccountNotVerifiedException ex, HttpServletRequest request) {
        logger.warn("Account not verified: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCOUNT_NOT_VERIFIED",
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(VerificationCodeExpiredException.class) //responsible for all error that throw this
    public ResponseEntity<ErrorResponse> handleVerificationCodeExpiredException(
            VerificationCodeExpiredException ex, HttpServletRequest request) {
        logger.warn("Verification code expired: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "VERIFICATION_CODE_EXPIRED",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCodeException(
            InvalidVerificationCodeException ex, HttpServletRequest request) {
        logger.warn("Invalid verification code: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_VERIFICATION_CODE",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyVerifiedException(
            AccountAlreadyVerifiedException ex, HttpServletRequest request) {
        logger.warn("Account already verified: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCOUNT_ALREADY_VERIFIED",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Spring Security Exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "AUTHENTICATION_FAILED",
                "Invalid credentials",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BAD_CREDENTIALS",
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.add(new ErrorResponse.FieldError(fieldName, rejectedValue, errorMessage));
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_FAILED",
                "Input validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        errorResponse.setFieldErrors(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            Object rejectedValue = violation.getInvalidValue();
            String errorMessage = violation.getMessage();
            fieldErrors.add(new ErrorResponse.FieldError(fieldName, rejectedValue, errorMessage));
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "CONSTRAINT_VIOLATION",
                "Constraint validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        errorResponse.setFieldErrors(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // IllegalArgumentException Handler
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // IllegalStateException Handler
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        logger.warn("Illegal state: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_STATE",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}

