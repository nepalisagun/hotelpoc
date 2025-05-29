package com.galaxyviewtower.hotel.crud.actuator;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheHealthIndicator implements HealthIndicator {

    private final CacheManager cacheManager;

    public CacheHealthIndicator(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        Map<String, Object> cacheDetails = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                CacheStats stats = caffeineCache.stats();
                
                Map<String, Object> statsDetails = new HashMap<>();
                statsDetails.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                statsDetails.put("missRate", String.format("%.2f%%", stats.missRate() * 100));
                statsDetails.put("loadSuccessRate", String.format("%.2f%%", stats.loadSuccessRate() * 100));
                statsDetails.put("loadFailureRate", String.format("%.2f%%", stats.loadFailureRate() * 100));
                statsDetails.put("evictionCount", stats.evictionCount());
                statsDetails.put("averageLoadPenalty", String.format("%.2f ms", stats.averageLoadPenalty()));
                
                cacheDetails.put(cacheName, statsDetails);
            }
        });

        details.put("caches", cacheDetails);
        details.put("totalCaches", cacheManager.getCacheNames().size());
        details.put("status", "UP");

        return Health.up()
            .withDetails(details)
            .build();
    }
} 