package com.galaxyviewtower.hotel.payment.service;

import com.galaxyviewtower.hotel.payment.dto.request.PaymentRequest;
import com.galaxyviewtower.hotel.payment.dto.response.PaymentResponse;
import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<PaymentResponse> initiatePayment(PaymentRequest request);
    Mono<PaymentResponse> processPaymentCallback(String paymentId, String status);
    Mono<PaymentResponse> refundPayment(String paymentId);
    Mono<PaymentResponse> getPaymentById(String paymentId);
    Mono<PaymentResponse> getPaymentByBookingId(String bookingId);
} 