package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceClientTest {

    private WebClient webClient;
    private WebClientConfig webClientConfig;
    private UserServiceClient userServiceClient;
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        webClientConfig = mock(WebClientConfig.class);
        userServiceClient = new UserServiceClient(webClient, webClientConfig);

        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestBodyUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(webClient.delete()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class), any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getUserById_ShouldHandleTimeout() {
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId("123");
        expectedUser.setName("Test User");

        when(responseSpec.bodyToMono(UserDTO.class))
            .thenReturn(Mono.just(expectedUser).delayElement(Duration.ofSeconds(6)));

        when(webClientConfig.applyResiliencePatterns("userService"))
            .thenReturn(mono -> mono.timeout(Duration.ofSeconds(5)));

        StepVerifier.create(userServiceClient.getUserById("123"))
            .expectError(RuntimeException.class)
            .verify(Duration.ofSeconds(10));
    }

    @Test
    void getUserById_ShouldRetryOnFailure() {
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId("123");
        expectedUser.setName("Test User");

        when(responseSpec.bodyToMono(UserDTO.class))
            .thenReturn(Mono.error(new IllegalStateException("Temporary error")))
            .thenReturn(Mono.just(expectedUser));

        when(webClientConfig.applyResiliencePatterns("userService"))
            .thenReturn(mono -> mono.retry(3));

        StepVerifier.create(userServiceClient.getUserById("123"))
            .expectNext(expectedUser)
            .verifyComplete();
    }

    @Test
    void createUser_ShouldHandleCircuitBreaker() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");

        when(responseSpec.bodyToMono(UserDTO.class))
            .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        when(webClientConfig.applyResiliencePatterns("userService"))
            .thenReturn(mono -> mono.transform(CircuitBreakerOperator.of(mock(CircuitBreaker.class))));

        StepVerifier.create(userServiceClient.createUser(userDTO))
            .expectError(RuntimeException.class)
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateUser_ShouldHandleSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("123");
        userDTO.setName("Updated User");

        when(responseSpec.bodyToMono(UserDTO.class))
            .thenReturn(Mono.just(userDTO));

        when(webClientConfig.applyResiliencePatterns("userService"))
            .thenReturn(mono -> mono);

        StepVerifier.create(userServiceClient.updateUser("123", userDTO))
            .expectNext(userDTO)
            .verifyComplete();
    }
} 