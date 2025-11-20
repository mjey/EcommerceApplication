package com.softwareinsight.UserService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Event DTO
 * Design Pattern: DTO Pattern for event communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {

    @JsonProperty("eventType")
    private String eventType;  // USER_CREATED, USER_UPDATED, USER_DELETED

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("timestamp")
    private Long timestamp;
}