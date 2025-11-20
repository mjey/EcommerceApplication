package com.softwareinsight.AuthService.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 *
 * Centralizes exception handling across all controllers
 *
 * Design Pattern: Exception Handling Pattern
 * @RestControllerAdvice applies to all @RestController classes
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = buildErrorResponse(
                "Validation Failed",
                errors,
                HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle user already exists exception
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        log.warn("User already exists: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                ex.getMessage(),
                null,
                HttpStatus.CONFLICT
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle bad credentials (wrong password)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {

        log.warn("Bad credentials: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                "Invalid username or password",
                null,
                HttpStatus.UNAUTHORIZED
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle user not found
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UsernameNotFoundException ex) {

        log.warn("User not found: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                "User not found",
                null,
                HttpStatus.NOT_FOUND
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);

        Map<String, Object> response = buildErrorResponse(
                "An unexpected error occurred",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Build standard error response
     */
    private Map<String, Object> buildErrorResponse(
            String message,
            Object details,
            HttpStatus status) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        if (details != null) {
            response.put("details", details);
        }

        return response;
    }
}
