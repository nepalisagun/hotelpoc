package com.galaxyviewtower.hotel.crud.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheMetricsService {

    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    public CacheMetricsService(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
    }

    public Map<String, CacheStats> getCacheStats() {
        Map<String, CacheStats> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache<?, ?> cache = (Cache<?, ?>) cacheManager.getCache(cacheName).getNativeCache();
            stats.put(cacheName, cache.stats());
            
            // Update metrics
            updateMetrics(cacheName, cache.stats());
        });
        
        return stats;
    }

    private void updateMetrics(String cacheName, CacheStats stats) {
        // Hit rate metrics
        meterRegistry.gauge("cache.hit.rate", 
            stats.hitRate());
        
        // Miss rate metrics
        meterRegistry.gauge("cache.miss.rate", 
            stats.missRate());
        
        // Load metrics
        meterRegistry.gauge("cache.load.success.rate", 
            stats.loadSuccessRate());
        meterRegistry.gauge("cache.load.failure.rate", 
            stats.loadFailureRate());
        
        // Eviction metrics
        meterRegistry.gauge("cache.eviction.count", 
            stats.evictionCount());
        
        // Average load time
        meterRegistry.gauge("cache.average.load.time", 
            stats.averageLoadPenalty());
        
        // Request count
        meterRegistry.gauge("cache.request.count", 
            stats.requestCount());
    }

    public void clearCache(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            cacheManager.getCache(cacheName).clear());
    }
} 