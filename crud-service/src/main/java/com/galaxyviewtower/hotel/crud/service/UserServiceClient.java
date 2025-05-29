package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

    private final WebClient webClient;
    private final WebClientConfig webClientConfig;

    public UserServiceClient(WebClient userServiceWebClient, WebClientConfig webClientConfig) {
        this.webClient = userServiceWebClient;
        this.webClientConfig = webClientConfig;
    }

    public Mono<UserDTO> getUserById(String userId) {
        return webClient.get()
            .uri("/api/users/{id}", userId)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("userService"));
    }

    public Mono<UserDTO> createUser(UserDTO userDTO) {
        return webClient.post()
            .uri("/api/users")
            .bodyValue(userDTO)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("userService"));
    }

    public Mono<UserDTO> updateUser(String userId, UserDTO userDTO) {
        return webClient.put()
            .uri("/api/users/{id}", userId)
            .bodyValue(userDTO)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("userService"));
    }

    public Mono<Void> deleteUser(String userId) {
        return webClient.delete()
            .uri("/api/users/{id}", userId)
            .retrieve()
            .bodyToMono(Void.class)
            .transform(webClientConfig.applyResiliencePatterns("userService"));
    }
} 