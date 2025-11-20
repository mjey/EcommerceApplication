package com.softwareinsight.UserService.service;

import com.softwareinsight.UserService.dto.UserEvent;
import com.softwareinsight.UserService.entity.UserProfile;
import com.softwareinsight.UserService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka Event Consumer
 *
 * Listens to user events from Auth Service
 *
 * Design Patterns:
 * - Event-Driven Architecture: Reacts to events from other services
 * - Observer Pattern: Observes events from Kafka
 *
 * @KafkaListener automatically consumes messages from a specified topic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final UserProfileRepository userProfileRepository;

    /**
     * Consume user events from Kafka
     *
     * Event-Driven Architecture: This service reacts to events from Auth Service
     * without direct coupling
     */
    @KafkaListener(
            topics = "user-events",
            groupId = "users-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeUserEvent(UserEvent event) {
        log.info("Received user event: {}", event);

        try {
            switch (event.getEventType()) {
                case "USER_CREATED":
                    handleUserCreated(event);
                    break;
                case "USER_UPDATED":
                    handleUserUpdated(event);
                    break;
                case "USER_DELETED":
                    handleUserDeleted(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
            // In production, you might want to:
            // - Send to dead letter queue
            // - Retry with exponential backoff
            // - Alert monitoring system
        }
    }

    /**
     * Handle USER_CREATED event
     */
    private void handleUserCreated(UserEvent event) {
        log.info("Creating user profile for user: {}", event.getUsername());

        // Check if a profile already exists (idempotency)
        if (userProfileRepository.existsById(event.getUserId())) {
            log.warn("User profile already exists for userId: {}", event.getUserId());
            return;
        }

        UserProfile profile = UserProfile.builder()
                .userId(event.getUserId())
                .username(event.getUsername())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .active(true)
                .build();

        userProfileRepository.save(profile);
        log.info("User profile created successfully for user: {}", event.getUsername());
    }

    /**
     * Handle USER_UPDATED event
     */
    private void handleUserUpdated(UserEvent event) {
        log.info("Updating user profile for user: {}", event.getUsername());

        userProfileRepository.findById(event.getUserId())
                .ifPresentOrElse(
                        profile -> {
                            profile.setUsername(event.getUsername());
                            profile.setEmail(event.getEmail());
                            profile.setFirstName(event.getFirstName());
                            profile.setLastName(event.getLastName());
                            userProfileRepository.save(profile);
                            log.info("User profile updated successfully");
                        },
                        () -> log.warn("User profile not found for userId: {}", event.getUserId())
                );
    }

    /**
     * Handle USER_DELETED event
     */
    private void handleUserDeleted(UserEvent event) {
        log.info("Deleting user profile for user: {}", event.getUsername());

        userProfileRepository.deleteById(event.getUserId());
        log.info("User profile deleted successfully");
    }
}