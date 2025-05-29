package com.galaxyviewtower.hotel.booking.integration;

import com.galaxyviewtower.hotel.booking.dto.request.BookingRequestDto;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("rate-limit-test")
class RateLimiterFallbackTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    private BookingRequestDto testBookingRequest;
    private RateLimiter bookingServiceRateLimiter;

    @BeforeEach
    void setUp() {
        bookingServiceRateLimiter = rateLimiterRegistry.rateLimiter("bookingService");

        testBookingRequest = new BookingRequestDto();
        testBookingRequest.setHotelId("test-hotel-id");
        testBookingRequest.setRoomTypeId("test-room-type-id");
        testBookingRequest.setUserId("test-user-id");
        testBookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        testBookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        testBookingRequest.setNumberOfGuests(2);
    }

    @Test
    void testRateLimiterFallbackWithRetry() {
        AtomicInteger attemptCount = new AtomicInteger(0);
        AtomicInteger fallbackCount = new AtomicInteger(0);

        // Create a request that will be rate limited
        Mono<?> request = bookingService.createBooking(testBookingRequest)
                .doOnSubscribe(s -> attemptCount.incrementAndGet())
                .onErrorResume(e -> {
                    if (e.getMessage().contains("Rate limit exceeded")) {
                        fallbackCount.incrementAndGet();
                        // Simulate a fallback response
                        return Mono.just("Fallback response");
                    }
                    return Mono.error(e);
                });

        // Execute multiple requests to trigger rate limiting
        for (int i = 0; i < 4; i++) {
            StepVerifier.create(request)
                    .expectNextCount(1)
                    .verifyComplete();
        }

        // Verify that we had multiple attempts and fallbacks
        assertTrue(attemptCount.get() > 1, "Should have multiple attempts");
        assertTrue(fallbackCount.get() > 0, "Should have fallback responses");
    }

    @Test
    void testRateLimiterFallbackWithTimeout() {
        AtomicInteger timeoutCount = new AtomicInteger(0);

        // Create a request that will timeout
        Mono<?> request = bookingService.createBooking(testBookingRequest)
                .timeout(Duration.ofMillis(100))
                .onErrorResume(e -> {
                    if (e instanceof java.util.concurrent.TimeoutException) {
                        timeoutCount.incrementAndGet();
                        return Mono.just("Timeout fallback response");
                    }
                    return Mono.error(e);
                });

        // Execute the request
        StepVerifier.create(request)
                .expectNext("Timeout fallback response")
                .verifyComplete();

        assertEquals(1, timeoutCount.get(), "Should have one timeout fallback");
    }

    @Test
    void testRateLimiterFallbackWithCircuitBreaker() {
        AtomicInteger circuitBreakerCount = new AtomicInteger(0);

        // Create a request that will trigger circuit breaker
        Mono<?> request = bookingService.createBooking(testBookingRequest)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("Circuit breaker is open")) {
                        circuitBreakerCount.incrementAndGet();
                        return Mono.just("Circuit breaker fallback response");
                    }
                    return Mono.error(e);
                });

        // Execute multiple requests to trigger circuit breaker
        for (int i = 0; i < 10; i++) {
            StepVerifier.create(request)
                    .expectNextCount(1)
                    .verifyComplete();
        }

        assertTrue(circuitBreakerCount.get() > 0, "Should have circuit breaker fallbacks");
    }

    @Test
    void testRateLimiterFallbackWithBulkhead() {
        AtomicInteger bulkheadCount = new AtomicInteger(0);

        // Create a request that will trigger bulkhead
        Mono<?> request = bookingService.createBooking(testBookingRequest)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("Bulkhead is full")) {
                        bulkheadCount.incrementAndGet();
                        return Mono.just("Bulkhead fallback response");
                    }
                    return Mono.error(e);
                });

        // Execute multiple concurrent requests to trigger bulkhead
        for (int i = 0; i < 5; i++) {
            StepVerifier.create(request)
                    .expectNextCount(1)
                    .verifyComplete();
        }

        assertTrue(bulkheadCount.get() > 0, "Should have bulkhead fallbacks");
    }
} 