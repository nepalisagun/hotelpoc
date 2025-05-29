package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.dto.BookingDTO;
import com.galaxyviewtower.hotel.crud.dto.PaymentDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CachePerformanceTest {

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private CacheMetricsService cacheMetricsService;

    @Autowired
    private CachePerformanceReportGenerator reportGenerator;

    private ExecutorService executorService;
    private Map<String, Double> performanceMetrics;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(10);
        cacheMetricsService.clearAllCaches();
        performanceMetrics = new HashMap<>();
    }

    @AfterAll
    static void tearDown() {
        // Clean up any resources if needed
    }

    @Test
    void testConcurrentCacheAccess() throws InterruptedException {
        // Arrange
        int numberOfThreads = 100;
        int requestsPerThread = 10;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        String bookingId = UUID.randomUUID().toString();
                        try {
                            StepVerifier.create(bookingServiceClient.getBookingById(bookingId))
                                .expectNextMatches(booking -> 
                                    booking.getId().equals(bookingId) &&
                                    booking.getStatus().equals("FALLBACK"))
                                .verifyComplete();
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Assert
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        assertEquals(numberOfThreads * requestsPerThread, successCount.get());
        assertEquals(0, failureCount.get());

        // Calculate performance metrics
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        double requestsPerSecond = (numberOfThreads * requestsPerThread) / totalTime;
        performanceMetrics.put("Concurrent Requests Total Time (s)", totalTime);
        performanceMetrics.put("Requests Per Second", requestsPerSecond);

        // Verify cache metrics
        var stats = cacheMetricsService.getCacheStats();
        assertTrue(stats.get("bookingFallback").hitRate() > 0.5);

        // Generate report
        reportGenerator.generateReport(stats, performanceMetrics);
    }

    @Test
    void testCacheEviction() {
        // Arrange
        String bookingId = UUID.randomUUID().toString();
        BookingDTO booking = createSampleBooking(bookingId);
        long startTime = System.currentTimeMillis();

        // Act & Assert
        // First call - should miss cache
        StepVerifier.create(bookingServiceClient.getBookingById(bookingId))
            .expectNextMatches(b -> b.getId().equals(bookingId))
            .verifyComplete();

        // Second call - should hit cache
        StepVerifier.create(bookingServiceClient.getBookingById(bookingId))
            .expectNextMatches(b -> b.getId().equals(bookingId))
            .verifyComplete();

        // Calculate performance metrics
        long endTime = System.currentTimeMillis();
        double averageResponseTime = (endTime - startTime) / 2.0;
        performanceMetrics.put("Average Response Time (ms)", averageResponseTime);

        // Verify cache hit rate
        var stats = cacheMetricsService.getCacheStats();
        assertTrue(stats.get("bookingFallback").hitRate() > 0.0);

        // Generate report
        reportGenerator.generateReport(stats, performanceMetrics);
    }

    @Test
    void testCachePerformanceUnderLoad() {
        // Arrange
        int numberOfRequests = 1000;
        long startTime = System.currentTimeMillis();

        // Act
        for (int i = 0; i < numberOfRequests; i++) {
            String paymentId = UUID.randomUUID().toString();
            PaymentDTO payment = createSamplePayment(paymentId);

            StepVerifier.create(paymentServiceClient.getPaymentStatus(paymentId))
                .expectNextMatches(p -> p.getId().equals(paymentId))
                .verifyComplete();
        }

        // Calculate performance metrics
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double averageTimePerRequest = (double) totalTime / numberOfRequests;
        double requestsPerSecond = numberOfRequests / (totalTime / 1000.0);

        performanceMetrics.put("Total Time (ms)", (double) totalTime);
        performanceMetrics.put("Average Time Per Request (ms)", averageTimePerRequest);
        performanceMetrics.put("Requests Per Second", requestsPerSecond);

        // Verify performance metrics
        assertTrue(averageTimePerRequest < 100); // Average time per request should be less than 100ms

        var stats = cacheMetricsService.getCacheStats();
        assertTrue(stats.get("paymentFallback").averageLoadPenalty() < 50); // Average load time should be less than 50ms

        // Generate report
        reportGenerator.generateReport(stats, performanceMetrics);
    }

    private BookingDTO createSampleBooking(String id) {
        return BookingDTO.builder()
            .id(id != null ? id : UUID.randomUUID().toString())
            .hotelId(UUID.randomUUID().toString())
            .userId(UUID.randomUUID().toString())
            .checkIn(LocalDateTime.now().plusDays(1))
            .checkOut(LocalDateTime.now().plusDays(3))
            .status("CONFIRMED")
            .createdAt(LocalDateTime.now())
            .build();
    }

    private PaymentDTO createSamplePayment(String id) {
        return PaymentDTO.builder()
            .id(id != null ? id : UUID.randomUUID().toString())
            .bookingId(UUID.randomUUID().toString())
            .amount(new java.math.BigDecimal("299.99"))
            .currency("USD")
            .status("COMPLETED")
            .createdAt(LocalDateTime.now())
            .build();
    }
} 