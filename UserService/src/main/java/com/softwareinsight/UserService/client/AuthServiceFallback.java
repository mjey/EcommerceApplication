package com.softwareinsight.UserService.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation when Auth Service is down
 *
 * Circuit Breaker Pattern: Graceful degradation
 */
@Component
@Slf4j
public class AuthServiceFallback implements AuthServiceClient {

    @Override
    public Map<String, Object> validateToken(Map<String, String> request) {
        log.error("Auth Service is unavailable. Circuit breaker activated!");

        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("valid", false);
        fallbackResponse.put("message", "Auth service is temporarily unavailable");
        fallbackResponse.put("circuitBreakerActivated", true);

        return fallbackResponse;
    }
}
