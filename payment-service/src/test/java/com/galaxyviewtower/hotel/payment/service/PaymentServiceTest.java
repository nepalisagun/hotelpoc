package com.galaxyviewtower.hotel.payment.service;

import com.galaxyviewtower.hotel.payment.dto.request.PaymentRequest;
import com.galaxyviewtower.hotel.payment.dto.response.PaymentResponse;
import com.galaxyviewtower.hotel.payment.model.Payment;
import com.galaxyviewtower.hotel.payment.repository.PaymentRepository;
import com.galaxyviewtower.hotel.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    private PaymentRequest validRequest;
    private Payment existingPayment;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository);

        validRequest = new PaymentRequest();
        validRequest.setBookingId(UUID.randomUUID().toString());
        validRequest.setUserId(UUID.randomUUID().toString());
        validRequest.setAmount(new BigDecimal("100.00"));
        validRequest.setCurrency("USD");
        validRequest.setPaymentGateway("STRIPE");
        validRequest.setPaymentMethodDetailsTokenized("tok_visa_123");
        validRequest.setIdempotencyKey("test_key_123");

        existingPayment = new Payment();
        existingPayment.setId(UUID.randomUUID().toString());
        existingPayment.setBookingId(validRequest.getBookingId());
        existingPayment.setUserId(validRequest.getUserId());
        existingPayment.setAmount(validRequest.getAmount());
        existingPayment.setCurrency(validRequest.getCurrency());
        existingPayment.setStatus(Payment.PaymentStatus.SUCCESS);
        existingPayment.setPaymentMethodDetailsTokenized(validRequest.getPaymentMethodDetailsTokenized());
        existingPayment.setPaymentGateway(validRequest.getPaymentGateway());
        existingPayment.setIdempotencyKey(validRequest.getIdempotencyKey());
    }

    @Test
    void initiatePayment_WithNewIdempotencyKey_ShouldCreateNewPayment() {
        when(paymentRepository.findByIdempotencyKey(validRequest.getIdempotencyKey()))
                .thenReturn(Mono.empty());
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(Mono.just(existingPayment));

        StepVerifier.create(paymentService.initiatePayment(validRequest))
                .expectNextMatches(response -> 
                    response.getBookingId().equals(validRequest.getBookingId()) &&
                    response.getStatus().equals(Payment.PaymentStatus.SUCCESS))
                .verifyComplete();
    }

    @Test
    void initiatePayment_WithExistingIdempotencyKey_ShouldReturnExistingPayment() {
        when(paymentRepository.findByIdempotencyKey(validRequest.getIdempotencyKey()))
                .thenReturn(Mono.just(existingPayment));

        StepVerifier.create(paymentService.initiatePayment(validRequest))
                .expectNextMatches(response -> 
                    response.getId().equals(existingPayment.getId()) &&
                    response.getStatus().equals(Payment.PaymentStatus.SUCCESS))
                .verifyComplete();
    }

    @Test
    void processPaymentCallback_ShouldUpdatePaymentStatus() {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Mono.just(payment));
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(Mono.just(payment));

        StepVerifier.create(paymentService.processPaymentCallback(paymentId, "SUCCESS"))
                .expectNextMatches(response -> 
                    response.getId().equals(paymentId) &&
                    response.getStatus().equals(Payment.PaymentStatus.SUCCESS))
                .verifyComplete();
    }

    @Test
    void refundPayment_ShouldUpdatePaymentStatus() {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentGateway("STRIPE");
        payment.setPaymentGatewayTransactionId("ch_123");

        when(paymentRepository.findById(paymentId))
                .thenReturn(Mono.just(payment));
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(Mono.just(payment));

        StepVerifier.create(paymentService.refundPayment(paymentId))
                .expectNextMatches(response -> 
                    response.getId().equals(paymentId) &&
                    response.getStatus().equals(Payment.PaymentStatus.REFUNDED))
                .verifyComplete();
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setId(paymentId);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Mono.just(payment));

        StepVerifier.create(paymentService.getPaymentById(paymentId))
                .expectNextMatches(response -> response.getId().equals(paymentId))
                .verifyComplete();
    }

    @Test
    void getPaymentByBookingId_ShouldReturnPayment() {
        String bookingId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setBookingId(bookingId);

        when(paymentRepository.findByBookingId(bookingId))
                .thenReturn(Mono.just(payment));

        StepVerifier.create(paymentService.getPaymentByBookingId(bookingId))
                .expectNextMatches(response -> response.getBookingId().equals(bookingId))
                .verifyComplete();
    }
} 