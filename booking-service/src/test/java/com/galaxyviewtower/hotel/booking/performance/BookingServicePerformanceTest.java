package com.galaxyviewtower.hotel.booking.performance;

import com.galaxyviewtower.hotel.booking.dto.request.BookingRequestDto;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingServicePerformanceTest {

    @Autowired
    private BookingService bookingService;

    private BookingRequestDto testBookingRequest;
    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 10;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @BeforeEach
    void setUp() {
        testBookingRequest = new BookingRequestDto();
        testBookingRequest.setHotelId("test-hotel-id");
        testBookingRequest.setRoomTypeId("test-room-type-id");
        testBookingRequest.setUserId("test-user-id");
        testBookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        testBookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        testBookingRequest.setNumberOfGuests(2);
    }

    @Test
    void testConcurrentBookingCreation() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userIndex = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_USER; j++) {
                        long startTime = System.currentTimeMillis();
                        try {
                            StepVerifier.create(bookingService.createBooking(testBookingRequest)
                                    .timeout(TIMEOUT))
                                    .expectNextCount(1)
                                    .verifyComplete();
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                        long endTime = System.currentTimeMillis();
                        responseTimes.add(endTime - startTime);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        boolean completed = latch.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertTrue(completed, "Test did not complete within timeout");

        // Calculate statistics
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        // Print performance metrics
        System.out.println("Performance Test Results:");
        System.out.println("Total Requests: " + (CONCURRENT_USERS * REQUESTS_PER_USER));
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
        System.out.println("Average Response Time: " + avgResponseTime + "ms");
        System.out.println("Max Response Time: " + maxResponseTime + "ms");
        System.out.println("Min Response Time: " + minResponseTime + "ms");

        // Assertions
        assertTrue(successCount.get() > 0, "Should have some successful requests");
        assertTrue(avgResponseTime < 1000, "Average response time should be under 1 second");
    }

    @Test
    void testRateLimiterPerformance() {
        int totalRequests = 100;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rateLimitedCount = new AtomicInteger(0);
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < totalRequests; i++) {
            long startTime = System.currentTimeMillis();
            try {
                StepVerifier.create(bookingService.createBooking(testBookingRequest)
                        .timeout(Duration.ofSeconds(1)))
                        .expectNextCount(1)
                        .verifyComplete();
                successCount.incrementAndGet();
            } catch (Exception e) {
                if (e.getMessage().contains("Rate limit exceeded")) {
                    rateLimitedCount.incrementAndGet();
                }
            }
            long endTime = System.currentTimeMillis();
            responseTimes.add(endTime - startTime);
        }

        // Print rate limiter metrics
        System.out.println("Rate Limiter Test Results:");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Rate Limited Requests: " + rateLimitedCount.get());
        System.out.println("Average Response Time: " + 
                responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0) + "ms");

        // Assertions
        assertTrue(rateLimitedCount.get() > 0, "Should have some rate-limited requests");
        assertTrue(successCount.get() > 0, "Should have some successful requests");
    }
} 