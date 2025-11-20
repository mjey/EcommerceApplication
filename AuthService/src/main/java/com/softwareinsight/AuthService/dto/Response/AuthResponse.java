package com.softwareinsight.AuthService.dto.Response;

import com.softwareinsight.AuthService.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Long expiresIn;

    private String username;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
    private Long userId;

}
