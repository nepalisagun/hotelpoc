package com.galaxyviewtower.hotel.user.controller;

import com.galaxyviewtower.hotel.user.dto.request.LoginRequest;
import com.galaxyviewtower.hotel.user.dto.request.UserRegistrationRequest;
import com.galaxyviewtower.hotel.user.dto.response.AuthResponse;
import com.galaxyviewtower.hotel.user.dto.response.UserResponse;
import com.galaxyviewtower.hotel.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {

    private final UserService userService;

    @PostMapping("/users/register")
    @RateLimiter(name = "userRegistration")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public Mono<ResponseEntity<UserResponse>> registerUser(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/auth/login")
    @RateLimiter(name = "userLogin")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Mono<ResponseEntity<AuthResponse>> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest request) {
        return userService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/auth/refresh")
    @Operation(summary = "Refresh token", description = "Generates new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public Mono<ResponseEntity<AuthResponse>> refreshToken(
            @Parameter(description = "Refresh token in Authorization header", required = true)
            @RequestHeader("Authorization") String refreshToken) {
        return userService.refreshToken(refreshToken.replace("Bearer ", ""))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user details", description = "Retrieves user information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<ResponseEntity<UserResponse>> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Update user", description = "Updates user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<ResponseEntity<UserResponse>> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Updated user details", required = true)
            @Valid @RequestBody UserRegistrationRequest request) {
        return userService.updateUser(userId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Deactivate user", description = "Deactivates a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Mono<ResponseEntity<Void>> deactivateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return userService.deactivateUser(userId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
} 