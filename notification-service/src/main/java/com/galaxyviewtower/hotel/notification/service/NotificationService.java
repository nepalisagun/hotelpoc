package com.galaxyviewtower.hotel.notification.service;

import com.galaxyviewtower.hotel.notification.dto.request.NotificationRequest;
import reactor.core.publisher.Mono;

public interface NotificationService {
    Mono<Void> sendNotification(NotificationRequest request);
    Mono<Void> processBookingConfirmation(String bookingId, String userId);
    Mono<Void> processPaymentConfirmation(String paymentId, String userId);
    Mono<Void> processCancellationNotice(String bookingId, String userId);
} 