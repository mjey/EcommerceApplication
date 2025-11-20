package com.softwareinsight.AuthService.controller;

import com.softwareinsight.AuthService.dto.Request.LoginRequest;
import com.softwareinsight.AuthService.dto.Request.RegisterRequest;
import com.softwareinsight.AuthService.dto.Request.ValidateTokenRequest;
import com.softwareinsight.AuthService.dto.Response.AuthResponse;
import com.softwareinsight.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller
 *
 * REST API endpoints for authentication
 *
 * @RestController = @Controller + @ResponseBody
 * @RequestMapping defines a base path for all endpoints
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * Register new user
     *
     * POST /api/v1/auth/register
     *
     * @Valid triggers validation on RegisterRequest
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration request received for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login user
     *
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login request received for: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate token
     *
     * POST /api/v1/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Valid @RequestBody ValidateTokenRequest request) {

        log.debug("Token validation request received");
        Map<String, Object> response = authService.validateToken(request.getToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "auth-service"
        ));
    }
}
