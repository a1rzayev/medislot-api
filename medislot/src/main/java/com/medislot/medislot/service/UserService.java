package com.medislot.medislot.service;

import com.medislot.medislot.dto.auth.AuthResponse;
import com.medislot.medislot.dto.auth.LoginRequest;
import com.medislot.medislot.dto.auth.RegisterRequest;
import com.medislot.medislot.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Login user
     */
    AuthResponse login(LoginRequest request);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by ID
     */
    Optional<User> findById(UUID id);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}
