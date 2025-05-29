package com.galaxyviewtower.hotel.user.integration;

import com.galaxyviewtower.hotel.user.dto.request.LoginRequest;
import com.galaxyviewtower.hotel.user.dto.request.UserRegistrationRequest;
import com.galaxyviewtower.hotel.user.dto.response.AuthResponse;
import com.galaxyviewtower.hotel.user.dto.response.UserResponse;
import com.galaxyviewtower.hotel.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserRegistrationRequest testRegistrationRequest;
    private LoginRequest testLoginRequest;

    @BeforeEach
    void setUp() {
        String username = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        testRegistrationRequest = new UserRegistrationRequest();
        testRegistrationRequest.setUsername(username);
        testRegistrationRequest.setEmail(username + "@example.com");
        testRegistrationRequest.setPassword("Test@123");
        testRegistrationRequest.setFirstName("Test");
        testRegistrationRequest.setLastName("User");

        testLoginRequest = new LoginRequest();
        testLoginRequest.setUsernameOrEmail(username);
        testLoginRequest.setPassword("Test@123");
    }

    @Test
    void testUserRegistrationAndLogin() {
        // Register user
        StepVerifier.create(userService.registerUser(testRegistrationRequest))
                .assertNext(userResponse -> {
                    assertNotNull(userResponse.getId());
                    assertEquals(testRegistrationRequest.getUsername(), userResponse.getUsername());
                    assertEquals(testRegistrationRequest.getEmail(), userResponse.getEmail());
                    assertEquals(testRegistrationRequest.getFirstName(), userResponse.getFirstName());
                    assertEquals(testRegistrationRequest.getLastName(), userResponse.getLastName());
                })
                .verifyComplete();

        // Login with registered user
        StepVerifier.create(userService.login(testLoginRequest))
                .assertNext(authResponse -> {
                    assertNotNull(authResponse.getToken());
                    assertNotNull(authResponse.getRefreshToken());
                    assertNotNull(authResponse.getUser());
                    assertEquals(testRegistrationRequest.getUsername(), authResponse.getUser().getUsername());
                })
                .verifyComplete();
    }

    @Test
    void testUserUpdate() {
        // Register user first
        UserResponse registeredUser = userService.registerUser(testRegistrationRequest).block();
        assertNotNull(registeredUser);

        // Update user
        UserRegistrationRequest updateRequest = new UserRegistrationRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setPassword("NewTest@123");

        StepVerifier.create(userService.updateUser(registeredUser.getId(), updateRequest))
                .assertNext(updatedUser -> {
                    assertEquals(registeredUser.getId(), updatedUser.getId());
                    assertEquals(updateRequest.getFirstName(), updatedUser.getFirstName());
                    assertEquals(updateRequest.getLastName(), updatedUser.getLastName());
                })
                .verifyComplete();
    }

    @Test
    void testTokenRefresh() {
        // Register and login user
        userService.registerUser(testRegistrationRequest).block();
        AuthResponse authResponse = userService.login(testLoginRequest).block();
        assertNotNull(authResponse);

        // Test token refresh
        StepVerifier.create(userService.refreshToken(authResponse.getRefreshToken()))
                .assertNext(newAuthResponse -> {
                    assertNotNull(newAuthResponse.getToken());
                    assertNotNull(newAuthResponse.getRefreshToken());
                    assertNotEquals(authResponse.getToken(), newAuthResponse.getToken());
                })
                .verifyComplete();
    }

    @Test
    void testUserDeactivation() {
        // Register user
        UserResponse registeredUser = userService.registerUser(testRegistrationRequest).block();
        assertNotNull(registeredUser);

        // Deactivate user
        StepVerifier.create(userService.deactivateUser(registeredUser.getId()))
                .verifyComplete();

        // Verify user is deactivated
        StepVerifier.create(userService.getUserById(registeredUser.getId()))
                .assertNext(user -> assertFalse(user.isActive()))
                .verifyComplete();
    }

    @Test
    void testInvalidLogin() {
        // Register user
        userService.registerUser(testRegistrationRequest).block();

        // Try login with wrong password
        LoginRequest invalidLoginRequest = new LoginRequest();
        invalidLoginRequest.setUsernameOrEmail(testRegistrationRequest.getUsername());
        invalidLoginRequest.setPassword("WrongPassword");

        StepVerifier.create(userService.login(invalidLoginRequest))
                .expectError(RuntimeException.class)
                .verify();
    }
} 