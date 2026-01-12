package com.medislot.demo.service;

import com.medislot.demo.dto.auth.AuthResponse;
import com.medislot.demo.dto.auth.LoginRequest;
import com.medislot.demo.dto.auth.RegisterRequest;
import com.medislot.demo.entity.User;

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
