package com.galaxyviewtower.hotel.payment.repository;

import com.galaxyviewtower.hotel.payment.model.Payment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends R2dbcRepository<Payment, String> {
    Mono<Payment> findByBookingId(String bookingId);
    Mono<Payment> findByPaymentGatewayTransactionId(String transactionId);
    Mono<Payment> findByIdempotencyKey(String idempotencyKey);
} 