package com.galaxyviewtower.hotel.crud.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private final MeterRegistry meterRegistry;

    public CacheConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure Caffeine with eviction strategies
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            // Time-based eviction
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            // Size-based eviction
            .maximumSize(1000)
            .maximumWeight(10000)
            .weigher((key, value) -> {
                if (value instanceof String) return ((String) value).length();
                return 1;
            })
            // Reference-based eviction
            .weakKeys()
            .weakValues()
            // Statistics
            .recordStats()
            // Removal listener
            .removalListener(notification -> 
                meterRegistry.counter("cache.removals", 
                    "cache", notification.getKey().toString(),
                    "cause", notification.getCause().toString())
                .increment());

        cacheManager.setCaffeine(caffeine);
        
        // Register cache metrics
        meterRegistry.gauge("cache.size", cacheManager.getCache("bookingFallback"), cache -> 
            ((com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache()).estimatedSize());
        meterRegistry.gauge("cache.weight", cacheManager.getCache("bookingFallback"), cache -> 
            ((com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache()).estimatedSize());
        
        return cacheManager;
    }

    @Bean
    public CacheMetricsService cacheMetricsService(CacheManager cacheManager) {
        return new CacheMetricsService(cacheManager, meterRegistry);
    }
} 