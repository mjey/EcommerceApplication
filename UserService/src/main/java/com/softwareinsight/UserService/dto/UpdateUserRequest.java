package com.softwareinsight.UserService.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;
}