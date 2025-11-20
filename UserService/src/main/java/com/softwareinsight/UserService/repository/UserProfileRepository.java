package com.softwareinsight.UserService.repository;

import com.softwareinsight.UserService.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Pattern: Abstracts data access
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}