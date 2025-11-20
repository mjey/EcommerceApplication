package com.softwareinsight.UserService.service;

import com.softwareinsight.UserService.dto.UpdateUserRequest;
import com.softwareinsight.UserService.dto.UserProfileResponse;
import com.softwareinsight.UserService.entity.UserProfile;
import com.softwareinsight.UserService.exception.UserNotFoundException;
import com.softwareinsight.UserService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service
 *
 * Business logic for user profile management
 *
 * Design Pattern: Service Layer Pattern
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserProfileRepository userProfileRepository;

    /**
     * Get a user profile by ID
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        log.debug("Fetching user profile for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return mapToResponse(profile);
    }

    /**
     * Get user profile by username
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByUsername(String username) {
        log.debug("Fetching user profile for username: {}", username);

        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return mapToResponse(profile);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        log.debug("Fetching all user profiles");

        return userProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UpdateUserRequest request) {
        log.info("Updating user profile for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update only non-null fields
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            profile.setPostalCode(request.getPostalCode());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        UserProfile updatedProfile = userProfileRepository.save(profile);
        log.info("User profile updated successfully for userId: {}", userId);

        return mapToResponse(updatedProfile);
    }

    /**
     * Update last login timestamp
     */
    @Transactional
    public void updateLastLogin(Long userId) {
        log.debug("Updating last login for userId: {}", userId);

        userProfileRepository.findById(userId)
                .ifPresent(profile -> {
                    profile.setLastLoginAt(LocalDateTime.now());
                    userProfileRepository.save(profile);
                });
    }

    /**
     * Deactivate user
     */
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Deactivating user with userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        profile.setActive(false);
        userProfileRepository.save(profile);

        log.info("User deactivated successfully");
    }

    /**
     * Map entity to response DTO
     */
    private UserProfileResponse mapToResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .bio(profile.getBio())
                .address(profile.getAddress())
                .city(profile.getCity())
                .country(profile.getCountry())
                .postalCode(profile.getPostalCode())
                .avatarUrl(profile.getAvatarUrl())
                .active(profile.getActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .lastLoginAt(profile.getLastLoginAt())
                .build();
    }
}
