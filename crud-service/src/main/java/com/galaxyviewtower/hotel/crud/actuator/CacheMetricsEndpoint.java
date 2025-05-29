package com.galaxyviewtower.hotel.crud.actuator;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "cachemetrics")
public class CacheMetricsEndpoint {

    private final CacheManager cacheManager;

    public CacheMetricsEndpoint(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @ReadOperation
    public Map<String, Object> getCacheMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> cacheMetrics = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                CacheStats stats = caffeineCache.stats();
                
                Map<String, Object> statsMetrics = new HashMap<>();
                statsMetrics.put("hitCount", stats.hitCount());
                statsMetrics.put("missCount", stats.missCount());
                statsMetrics.put("loadSuccessCount", stats.loadSuccessCount());
                statsMetrics.put("loadFailureCount", stats.loadFailureCount());
                statsMetrics.put("totalLoadTime", stats.totalLoadTime());
                statsMetrics.put("evictionCount", stats.evictionCount());
                statsMetrics.put("evictionWeight", stats.evictionWeight());
                
                cacheMetrics.put(cacheName, statsMetrics);
            }
        });

        metrics.put("cacheMetrics", cacheMetrics);
        metrics.put("timestamp", System.currentTimeMillis());
        
        return metrics;
    }
} 