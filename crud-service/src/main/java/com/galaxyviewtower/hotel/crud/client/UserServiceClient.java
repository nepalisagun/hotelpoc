package com.galaxyviewtower.hotel.crud.client;

import com.galaxyviewtower.hotel.crud.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient userServiceClient;

    @Autowired
    public UserServiceClient(WebClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService", fallbackMethod = "getUserFallback")
    public Mono<UserDTO> getUserById(String userId) {
        return userServiceClient
                .get()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    @Retry(name = "userService", fallbackMethod = "validateUserFallback")
    public Mono<Boolean> validateUser(String userId) {
        return userServiceClient
                .get()
                .uri("/api/v1/users/{id}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    }
                    return Mono.error(e);
                });
    }

    // Fallback methods
    private Mono<UserDTO> getUserFallback(String userId, Exception e) {
        // Log the error
        return Mono.error(new ServiceException(
                "Failed to get user: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }

    private Mono<Boolean> validateUserFallback(String userId, Exception e) {
        // Log the error
        return Mono.just(false);
    }
} 