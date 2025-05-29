package com.galaxyviewtower.hotel.booking.controller;

import com.galaxyviewtower.hotel.booking.dto.AvailabilityRequest;
import com.galaxyviewtower.hotel.booking.dto.AvailabilityResponse;
import com.galaxyviewtower.hotel.booking.service.AvailabilityService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;
    private final Bucket bucket;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
        // Configure rate limiting: 300 requests per minute
        Bandwidth limit = Bandwidth.simple(300, Duration.ofMinutes(1));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping("/check")
    public Mono<ResponseEntity<AvailabilityResponse>> checkAvailability(
            @Valid @RequestBody AvailabilityRequest request) {
        if (!bucket.tryConsume(1)) {
            return Mono.just(ResponseEntity.tooManyRequests().build());
        }
        return availabilityService.checkAvailability(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{hotelId}/room-types/{roomTypeId}")
    public Mono<ResponseEntity<AvailabilityResponse>> getDetailedAvailability(
            @PathVariable String hotelId,
            @PathVariable String roomTypeId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {
        if (!bucket.tryConsume(1)) {
            return Mono.just(ResponseEntity.tooManyRequests().build());
        }
        return availabilityService.getDetailedAvailability(hotelId, roomTypeId, checkInDate, checkOutDate)
                .map(ResponseEntity::ok);
    }
} 