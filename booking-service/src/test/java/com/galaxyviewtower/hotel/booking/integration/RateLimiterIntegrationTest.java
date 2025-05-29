package com.galaxyviewtower.hotel.booking.integration;

import com.galaxyviewtower.hotel.booking.annotation.RateLimited;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("rate-limit-test")
class RateLimiterIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    private BookingRequestDto testBookingRequest;
    private RateLimiter bookingServiceRateLimiter;
    private RateLimiter availabilityCheckRateLimiter;
    private RateLimiter userBookingsRateLimiter;

    @BeforeEach
    void setUp() {
        bookingServiceRateLimiter = rateLimiterRegistry.rateLimiter("bookingService");
        availabilityCheckRateLimiter = rateLimiterRegistry.rateLimiter("availabilityCheck");
        userBookingsRateLimiter = rateLimiterRegistry.rateLimiter("userBookings");

        testBookingRequest = new BookingRequestDto();
        testBookingRequest.setHotelId("test-hotel-id");
        testBookingRequest.setRoomTypeId("test-room-type-id");
        testBookingRequest.setUserId("test-user-id");
        testBookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        testBookingRequest.setCheckOutDate(LocalDate.now().plusDays(2));
        testBookingRequest.setNumberOfGuests(2);
    }

    @Test
    void testBookingServiceRateLimit() {
        List<Mono<?>> requests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Create 4 requests (2 should succeed, 2 should fail)
        for (int i = 0; i < 4; i++) {
            requests.add(bookingService.createBooking(testBookingRequest)
                    .doOnSuccess(response -> successCount.incrementAndGet())
                    .doOnError(error -> failureCount.incrementAndGet())
                    .onErrorResume(e -> Mono.empty()));
        }

        // Execute all requests concurrently
        Mono.when(requests).block(Duration.ofSeconds(5));

        // Verify results
        assertEquals(2, successCount.get(), "Should have 2 successful requests");
        assertEquals(2, failureCount.get(), "Should have 2 failed requests");
        assertTrue(bookingServiceRateLimiter.getMetrics().getAvailablePermissions() == 0,
                "Rate limiter should be exhausted");
    }

    @Test
    void testAvailabilityCheckRateLimit() {
        List<Mono<?>> requests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Create 5 requests (3 should succeed, 2 should fail)
        for (int i = 0; i < 5; i++) {
            requests.add(bookingService.checkRoomAvailability(
                    testBookingRequest.getHotelId(),
                    testBookingRequest.getRoomTypeId(),
                    testBookingRequest.getCheckInDate(),
                    testBookingRequest.getCheckOutDate(),
                    1
            ).doOnSuccess(response -> successCount.incrementAndGet())
                    .doOnError(error -> failureCount.incrementAndGet())
                    .onErrorResume(e -> Mono.empty()));
        }

        // Execute all requests concurrently
        Mono.when(requests).block(Duration.ofSeconds(5));

        // Verify results
        assertEquals(3, successCount.get(), "Should have 3 successful requests");
        assertEquals(2, failureCount.get(), "Should have 2 failed requests");
        assertTrue(availabilityCheckRateLimiter.getMetrics().getAvailablePermissions() == 0,
                "Rate limiter should be exhausted");
    }

    @Test
    void testUserBookingsRateLimit() {
        List<Mono<?>> requests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Create 4 requests (2 should succeed, 2 should fail)
        for (int i = 0; i < 4; i++) {
            requests.add(bookingService.getUserBookings(testBookingRequest.getUserId())
                    .doOnSuccess(response -> successCount.incrementAndGet())
                    .doOnError(error -> failureCount.incrementAndGet())
                    .onErrorResume(e -> Mono.empty()));
        }

        // Execute all requests concurrently
        Mono.when(requests).block(Duration.ofSeconds(5));

        // Verify results
        assertEquals(2, successCount.get(), "Should have 2 successful requests");
        assertEquals(2, failureCount.get(), "Should have 2 failed requests");
        assertTrue(userBookingsRateLimiter.getMetrics().getAvailablePermissions() == 0,
                "Rate limiter should be exhausted");
    }

    @Test
    void testRateLimitRecovery() {
        // First, exhaust the rate limiter
        testBookingServiceRateLimit();

        // Wait for the rate limiter to refresh
        try {
            Thread.sleep(1100); // Wait slightly longer than the refresh period
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify that new requests can be made
        StepVerifier.create(bookingService.createBooking(testBookingRequest))
                .expectNextCount(1)
                .verifyComplete();

        assertTrue(bookingServiceRateLimiter.getMetrics().getAvailablePermissions() > 0,
                "Rate limiter should have recovered");
    }
} 