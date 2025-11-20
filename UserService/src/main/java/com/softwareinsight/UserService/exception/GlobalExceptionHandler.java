package com.softwareinsight.UserService.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Design Pattern: Exception Handling Pattern
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
     * Handle a user not found exception
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex) {

        log.warn("User not found: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                ex.getMessage(),
                null,
                HttpStatus.NOT_FOUND
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle Feign exceptions (inter-service communication failures)
     *
     * This is important for microservices - when calling other services fails
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(
            FeignException ex) {

        log.error("Feign client error: {}", ex.getMessage());

        String message = "Error communicating with external service";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Map Feign status codes to appropriate HTTP status
        if (ex.status() == 401 || ex.status() == 403) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authentication failed with external service";
        } else if (ex.status() == 404) {
            status = HttpStatus.NOT_FOUND;
            message = "Resource not found in external service";
        } else if (ex.status() >= 500) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "External service is currently unavailable";
        }

        Map<String, Object> response = buildErrorResponse(
                message,
                ex.getMessage(),
                status
        );

        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        log.warn("Illegal argument: {}", ex.getMessage());

        Map<String, Object> response = buildErrorResponse(
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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