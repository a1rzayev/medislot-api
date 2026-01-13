package com.medislot.medislot.controller;

import com.medislot.medislot.dto.ApiResponse;
import com.medislot.medislot.dto.auth.AuthResponse;
import com.medislot.medislot.dto.auth.LoginRequest;
import com.medislot.medislot.dto.auth.RegisterRequest;
import com.medislot.medislot.service.UserService;
import com.medislot.medislot.util.ResponseHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user registration and login
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication APIs - Register and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     * 
     * Roles:
     * - ADMIN: Can manage hospitals and doctors
     * - DOCTOR: Can manage their own slots and appointments
     * - PATIENT: Can create and manage their own appointments
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", 
               description = "Create a new user account with specified role (ADMIN, DOCTOR, or PATIENT)")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());
        
        AuthResponse authResponse = userService.register(request);
        
        logger.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseHelper.success(authResponse, "User registered successfully"));
    }

    /**
     * Login user
     * POST /api/auth/login
     * 
     * Returns JWT token for authentication
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", 
               description = "Authenticate user and receive JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse authResponse = userService.login(request);
        
        logger.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(
                ResponseHelper.success(authResponse, "Login successful"));
    }
}
