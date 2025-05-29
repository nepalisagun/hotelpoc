package com.galaxyviewtower.hotel.booking.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // 100 requests per period
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 1 minute period
                .timeoutDuration(Duration.ofSeconds(5)) // 5 seconds timeout
                .build();

        return RateLimiterRegistry.of(config);
    }
} 