package com.galaxyviewtower.hotel.notification.controller;

import com.galaxyviewtower.hotel.notification.dto.request.NotificationRequest;
import com.galaxyviewtower.hotel.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(
        summary = "Send a notification",
        description = "Sends a notification using the specified template"
    )
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    public Mono<ResponseEntity<Void>> sendNotification(@RequestBody NotificationRequest request) {
        return notificationService.sendNotification(request)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/booking/{bookingId}/confirmation")
    @Operation(
        summary = "Send booking confirmation",
        description = "Sends a booking confirmation notification"
    )
    @ApiResponse(responseCode = "200", description = "Booking confirmation sent successfully")
    public Mono<ResponseEntity<Void>> sendBookingConfirmation(
            @PathVariable String bookingId,
            @RequestParam String userId) {
        return notificationService.processBookingConfirmation(bookingId, userId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/payment/{paymentId}/confirmation")
    @Operation(
        summary = "Send payment confirmation",
        description = "Sends a payment confirmation notification"
    )
    @ApiResponse(responseCode = "200", description = "Payment confirmation sent successfully")
    public Mono<ResponseEntity<Void>> sendPaymentConfirmation(
            @PathVariable String paymentId,
            @RequestParam String userId) {
        return notificationService.processPaymentConfirmation(paymentId, userId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/booking/{bookingId}/cancellation")
    @Operation(
        summary = "Send cancellation notice",
        description = "Sends a booking cancellation notification"
    )
    @ApiResponse(responseCode = "200", description = "Cancellation notice sent successfully")
    public Mono<ResponseEntity<Void>> sendCancellationNotice(
            @PathVariable String bookingId,
            @RequestParam String userId) {
        return notificationService.processCancellationNotice(bookingId, userId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
} 