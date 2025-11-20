package com.softwareinsight.AuthService.service;

import com.softwareinsight.AuthService.dto.Request.LoginRequest;
import com.softwareinsight.AuthService.dto.Request.RegisterRequest;
import com.softwareinsight.AuthService.dto.Response.AuthResponse;
import com.softwareinsight.AuthService.entity.Role;
import com.softwareinsight.AuthService.entity.User;
import com.softwareinsight.AuthService.exceptions.UserAlreadyExistsException;
import com.softwareinsight.AuthService.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_EVENTS_TOPIC = "user-events";

    /**
     * Register new user
     *
     * @Transactional ensures atomicity - either everything succeeds, or nothing does
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if a user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create a new user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        // Assign default role
        user.addRole(Role.ROLE_USER);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Publish user-created event to Kafka (Event-Driven Architecture)
        publishUserCreatedEvent(savedUser);

        // Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtService.generateToken(userDetails);

        return buildAuthResponse(savedUser, token);
    }

    /**
     * Login user
     */
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Find user entity
        User user = userRepository.findByUsernameOrEmail(
                userDetails.getUsername(),
                userDetails.getUsername()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token
        String token = jwtService.generateToken(userDetails);

        log.info("User logged in successfully: {}", user.getUsername());

        return buildAuthResponse(user, token);
    }

    /**
     * Validate token
     */
    public Map<String, Object> validateToken(String token) {
        log.debug("Validating token");

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate token
            Boolean isValid = jwtService.validateToken(token);

            if (isValid) {
                // Extract username
                String username = jwtService.extractUsername(token);

                // Load user
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Get user entity for additional info
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                response.put("valid", true);
                response.put("username", username);
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                response.put("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
            } else {
                response.put("valid", false);
                response.put("message", "Invalid or expired token");
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            response.put("valid", false);
            response.put("message", "Token validation failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Build authentication response
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(86400000L) // 24 hours
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getId())
                .roles(user.getRoles())
                .build();
    }

    /**
     * Publish user-created event to Kafka
     *
     * Event-Driven Architecture: Other services can listen to this event
     * For example, Email service can send welcome email, Analytics service can track new users
     */
    private void publishUserCreatedEvent(User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "USER_CREATED");
            event.put("userId", user.getId());
            event.put("username", user.getUsername());
            event.put("email", user.getEmail());
            event.put("firstName", user.getFirstName());
            event.put("lastName", user.getLastName());
            event.put("timestamp", System.currentTimeMillis());

            kafkaTemplate.send(USER_EVENTS_TOPIC, user.getId().toString(), event);
            log.info("Published USER_CREATED event for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to publish user created event: {}", e.getMessage());
            // Don't throw exception - event publishing failure shouldn't break registration
        }
    }
}
