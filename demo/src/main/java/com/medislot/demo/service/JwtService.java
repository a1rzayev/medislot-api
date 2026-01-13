package com.medislot.demo.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for JWT operations
 */
public interface JwtService {

    /**
     * Generate JWT token for user
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extract username (email) from token
     */
    String extractUsername(String token);

    /**
     * Validate token
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);
}
