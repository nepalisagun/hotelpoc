package com.galaxyviewtower.hotel.booking.client;

import com.galaxyviewtower.hotel.booking.dto.response.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient webClient;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService")
    @Retry(name = "userService")
    public Mono<UserResponse> getUserById(String userId) {
        return webClient.get()
                .uri(userServiceUrl + "/api/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class);
    }

    @CircuitBreaker(name = "userService")
    @Retry(name = "userService")
    public Mono<Boolean> validateUser(String userId) {
        return getUserById(userId)
                .map(user -> user.isActive())
                .onErrorReturn(false);
    }
} 