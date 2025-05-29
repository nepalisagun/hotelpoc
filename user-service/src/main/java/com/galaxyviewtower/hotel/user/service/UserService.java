package com.galaxyviewtower.hotel.user.service;

import com.galaxyviewtower.hotel.user.dto.request.LoginRequest;
import com.galaxyviewtower.hotel.user.dto.request.UserRegistrationRequest;
import com.galaxyviewtower.hotel.user.dto.response.AuthResponse;
import com.galaxyviewtower.hotel.user.dto.response.UserResponse;
import com.galaxyviewtower.hotel.user.model.User;
import com.galaxyviewtower.hotel.user.repository.UserRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @RateLimiter(name = "userRegistration")
    @Retry(name = "userService")
    public Mono<UserResponse> registerUser(UserRegistrationRequest request) {
        return userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail())
                .flatMap(existingUser -> Mono.error(new RuntimeException("Username or email already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User user = new User();
                    user.setId(UUID.randomUUID().toString());
                    user.setUsername(request.getUsername());
                    user.setEmail(request.getEmail());
                    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setRole(User.UserRole.CUSTOMER);
                    user.setActive(true);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .map(UserResponse::fromUser);
                }));
    }

    @RateLimiter(name = "userLogin")
    @Retry(name = "userService")
    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    return userRepository.save(user)
                            .map(savedUser -> {
                                String token = jwtConfig.generateToken(
                                        savedUser.getUsername(),
                                        savedUser.getId(),
                                        savedUser.getRole().name()
                                );
                                String refreshToken = jwtConfig.generateRefreshToken(savedUser.getUsername());
                                return new AuthResponse(
                                        token,
                                        refreshToken,
                                        UserResponse.fromUser(savedUser),
                                        jwtConfig.getExpiration()
                                );
                            });
                });
    }

    @Retry(name = "userService")
    public Mono<UserResponse> getUserById(String userId) {
        return userRepository.findById(userId)
                .map(UserResponse::fromUser)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    @Retry(name = "userService")
    public Mono<UserResponse> updateUser(String userId, UserRegistrationRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setUpdatedAt(LocalDateTime.now());
                    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                    }
                    return userRepository.save(user)
                            .map(UserResponse::fromUser);
                });
    }

    @Retry(name = "userService")
    public Mono<Void> deactivateUser(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    user.setActive(false);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user)
                            .then();
                });
    }

    @Retry(name = "userService")
    public Mono<AuthResponse> refreshToken(String refreshToken) {
        if (!jwtConfig.validateToken(refreshToken)) {
            return Mono.error(new RuntimeException("Invalid refresh token"));
        }

        String username = jwtConfig.extractUsername(refreshToken);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .map(user -> {
                    String newToken = jwtConfig.generateToken(
                            user.getUsername(),
                            user.getId(),
                            user.getRole().name()
                    );
                    String newRefreshToken = jwtConfig.generateRefreshToken(user.getUsername());
                    return new AuthResponse(
                            newToken,
                            newRefreshToken,
                            UserResponse.fromUser(user),
                            jwtConfig.getExpiration()
                    );
                });
    }
} 