package com.galaxyviewtower.hotel.crud.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ApiKeyRotationService {

    @Value("${app.api.key.rotation.enabled:true}")
    private boolean rotationEnabled;

    @Value("${app.api.key.rotation.interval:30d}")
    private Duration rotationInterval;

    @Value("${app.api.key.rotation.grace-period:7d}")
    private Duration gracePeriod;

    @Value("${app.api.key.rotation.max-keys:3}")
    private int maxKeys;

    private final Map<String, ApiKeyInfo> activeKeys = new ConcurrentHashMap<>();

    public record ApiKeyInfo(
        String key,
        Instant createdAt,
        Instant expiresAt,
        boolean isActive
    ) {}

    public Mono<String> generateNewApiKey() {
        if (!rotationEnabled) {
            return Mono.just(generateKey());
        }

        String newKey = generateKey();
        Instant now = Instant.now();
        ApiKeyInfo keyInfo = new ApiKeyInfo(
            newKey,
            now,
            now.plus(rotationInterval),
            true
        );

        activeKeys.put(newKey, keyInfo);
        cleanupOldKeys();
        return Mono.just(newKey);
    }

    public Mono<Boolean> validateApiKey(String apiKey) {
        if (!rotationEnabled) {
            return Mono.just(true);
        }

        ApiKeyInfo keyInfo = activeKeys.get(apiKey);
        if (keyInfo == null) {
            return Mono.just(false);
        }

        Instant now = Instant.now();
        boolean isValid = keyInfo.isActive() && 
            (now.isBefore(keyInfo.expiresAt()) || 
             now.isBefore(keyInfo.expiresAt().plus(gracePeriod)));

        return Mono.just(isValid);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void rotateKeys() {
        if (!rotationEnabled) {
            return;
        }

        Instant now = Instant.now();
        activeKeys.entrySet().removeIf(entry -> {
            ApiKeyInfo keyInfo = entry.getValue();
            return now.isAfter(keyInfo.expiresAt().plus(gracePeriod));
        });
    }

    private void cleanupOldKeys() {
        if (activeKeys.size() > maxKeys) {
            activeKeys.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().createdAt().compareTo(e1.getValue().createdAt()))
                .skip(maxKeys)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(activeKeys::remove);
        }
    }

    private String generateKey() {
        // Generate a secure random API key
        byte[] randomBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
} 