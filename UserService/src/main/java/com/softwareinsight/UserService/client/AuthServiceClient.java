package com.softwareinsight.UserService.client;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign Client for Auth Service
 *
 * Design Patterns:
 * - Client Pattern: Abstracts HTTP communication
 * - Circuit Breaker Pattern: Prevents cascading failures
 * - Retry Pattern: Automatic retry on transient failures
 *
 * @FeignClient uses Eureka for service discovery
 * name = service name registered in Eureka
 */
@FeignClient(
        name = "auth-service",
        fallback = AuthServiceFallback.class
)
public interface AuthServiceClient {
    /**
     * Validate JWT token with Auth Service
     */
    @PostMapping("/api/v1/auth/validate")
    @Retry(name = "authService")
    Map<String, Object> validateToken(@RequestBody Map<String, String> request);
}