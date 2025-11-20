package com.softwareinsight.UserService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String avatarUrl;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}