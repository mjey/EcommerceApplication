package com.softwareinsight.Gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 *
 * Provides fallback responses when circuit breakers are triggered
 * Design Pattern: Fallback Pattern / Circuit Breaker Pattern
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("Auth Service Circuit Breaker activated - fallback triggered");
        return createFallbackResponse("Auth Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> usersFallback() {
        log.warn("Users Service Circuit Breaker activated - fallback triggered");
        return createFallbackResponse("Users Service is temporarily unavailable. Please try again later.");
    }

//    @GetMapping("/orders")
//    public ResponseEntity<Map<String, Object>> ordersFallback() {
//        log.warn("Orders Service Circuit Breaker activated - fallback triggered");
//        return createFallbackResponse("Orders Service is temporarily unavailable. Please try again later.");
//    }
//
//    @GetMapping("/payments")
//    public ResponseEntity<Map<String, Object>> paymentsFallback() {
//        log.warn("Payments Service Circuit Breaker activated - fallback triggered");
//        return createFallbackResponse("Payments Service is temporarily unavailable. Please try again later.");
//    }

    /**
     * Create a consistent fallback response
     */
    private ResponseEntity<Map<String, Object>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", message);
        response.put("circuitBreakerActivated", true);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
