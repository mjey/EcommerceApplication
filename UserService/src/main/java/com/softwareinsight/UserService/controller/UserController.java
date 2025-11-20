package com.softwareinsight.UserService.controller;

import com.softwareinsight.UserService.client.AuthServiceClient;
import com.softwareinsight.UserService.dto.UpdateUserRequest;
import com.softwareinsight.UserService.dto.UserProfileResponse;
import com.softwareinsight.UserService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Controller
 *
 * Design Patterns:
 * - Controller Pattern
 * - DTO Pattern
 * - Dependency Injection
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthServiceClient authServiceClient;

    /**
     * Get user profile by ID
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("Get user profile request for userId: {}", userId);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            validateToken(authHeader);
        }

        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get a user profile by username
     * GET /api/v1/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUsername(
            @PathVariable String username) {

        log.info("Get user profile request for username: {}", username);

        UserProfileResponse profile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get all users
     * GET /api/v1/users
     */
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("Get all users request");

        // Optional: Validate token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            validateToken(authHeader);
        }

        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update user profile
     * PUT /api/v1/users/{userId}
     * Requires authentication
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Update user profile request for userId: {}", userId);

        // Validate token and get user info
        Map<String, Object> tokenValidation = validateToken(authHeader);

        // Check if the authenticated user is updating their own profile
        Long authenticatedUserId = ((Number) tokenValidation.get("userId")).longValue();

        if (!authenticatedUserId.equals(userId)) {
            // TODO: check if a user has an ADMIN role
            log.warn("User {} attempting to update profile of user {}", authenticatedUserId, userId);
        }

        UserProfileResponse updatedProfile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Deactivate a user account
     * DELETE /api/v1/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Deactivate user request for userId: {}", userId);

        // Validate token
        Map<String, Object> tokenValidation = validateToken(authHeader);
        Long authenticatedUserId = ((Number) tokenValidation.get("userId")).longValue();

        // Only allow users to deactivate their own account (or admins)
        if (!authenticatedUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You can only deactivate your own account"));
        }

        userService.deactivateUser(userId);

        return ResponseEntity.ok(Map.of(
                "message", "User account deactivated successfully",
                "userId", userId.toString()
        ));
    }

    /**
     * Update last login timestamp
     * POST /api/v1/users/{userId}/last-login
     * This would typically be called by Auth Service after a successful login
     */
    @PostMapping("/{userId}/last-login")
    public ResponseEntity<Map<String, String>> updateLastLogin(@PathVariable Long userId) {
        log.info("Update last login request for userId: {}", userId);

        userService.updateLastLogin(userId);

        return ResponseEntity.ok(Map.of(
                "message", "Last login updated successfully",
                "userId", userId.toString()
        ));
    }

    /**
     * Validate token with an Auth Service
     * Circuit Breaker pattern protects against Auth Service failures
     */
    private Map<String, Object> validateToken(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix

        Map<String, String> request = new HashMap<>();
        request.put("token", token);

        log.debug("Validating token with Auth Service");

        // Feign Client call with Circuit Breaker
        Map<String, Object> response = authServiceClient.validateToken(request);

        Boolean isValid = (Boolean) response.get("valid");

        if (isValid == null || !isValid) {
            log.warn("Invalid token received");
            throw new IllegalArgumentException("Invalid or expired token");
        }

        log.debug("Token validated successfully");
        return response;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "users-service"
        ));
    }

    /**
     * Test endpoint to check Auth Service connectivity
     * GET /api/v1/users/test/auth-service
     */
    @GetMapping("/test/auth-service")
    public ResponseEntity<Map<String, Object>> testAuthService(
            @RequestHeader("Authorization") String authHeader) {

        log.info("Testing Auth Service connectivity");

        try {
            Map<String, Object> validation = validateToken(authHeader);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Auth Service is reachable");
            response.put("validation", validation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("message", "Auth Service is unreachable");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}