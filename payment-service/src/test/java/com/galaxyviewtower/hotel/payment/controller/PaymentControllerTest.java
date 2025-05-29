package com.galaxyviewtower.hotel.payment.controller;

import com.galaxyviewtower.hotel.payment.dto.request.PaymentRequest;
import com.galaxyviewtower.hotel.payment.dto.response.PaymentResponse;
import com.galaxyviewtower.hotel.payment.model.Payment;
import com.galaxyviewtower.hotel.payment.service.PaymentService;
import com.galaxyviewtower.hotel.payment.service.impl.PaymentServiceImpl;
import com.galaxyviewtower.hotel.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentRepository paymentRepository;

    private PaymentRequest validRequest;
    private PaymentResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new PaymentRequest();
        validRequest.setBookingId(UUID.randomUUID().toString());
        validRequest.setUserId(UUID.randomUUID().toString());
        validRequest.setAmount(new BigDecimal("100.00"));
        validRequest.setCurrency("USD");
        validRequest.setPaymentGateway("STRIPE");
        validRequest.setPaymentMethodDetailsTokenized("tok_visa_123");
        validRequest.setIdempotencyKey("test_key_123");

        validResponse = PaymentResponse.fromPayment(new Payment());
        validResponse.setId(UUID.randomUUID().toString());
        validResponse.setBookingId(validRequest.getBookingId());
        validResponse.setUserId(validRequest.getUserId());
        validResponse.setAmount(validRequest.getAmount());
        validResponse.setCurrency(validRequest.getCurrency());
        validResponse.setStatus(Payment.PaymentStatus.SUCCESS);
    }

    @Test
    void initiatePayment_WithValidRequest_ShouldReturnSuccess() {
        when(paymentService.initiatePayment(any(PaymentRequest.class)))
                .thenReturn(Mono.just(validResponse));

        webTestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", "test_key_123")
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validResponse.getId())
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }

    @Test
    void processCallback_WithValidSignature_ShouldReturnSuccess() {
        when(paymentService.processPaymentCallback(any(String.class), any(String.class)))
                .thenReturn(Mono.just(validResponse));

        webTestClient.post()
                .uri("/api/v1/payments/{paymentId}/callback?status=SUCCESS", validResponse.getId())
                .header("Stripe-Signature", "test_signature")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validResponse.getId())
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }

    @Test
    void refundPayment_WithValidPayment_ShouldReturnSuccess() {
        when(paymentService.refundPayment(any(String.class)))
                .thenReturn(Mono.just(validResponse));

        webTestClient.post()
                .uri("/api/v1/payments/{paymentId}/refund", validResponse.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validResponse.getId())
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }

    @Test
    void getPaymentById_WithValidId_ShouldReturnPayment() {
        when(paymentService.getPaymentById(any(String.class)))
                .thenReturn(Mono.just(validResponse));

        webTestClient.get()
                .uri("/api/v1/payments/{paymentId}", validResponse.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validResponse.getId())
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }

    @Test
    void getPaymentByBookingId_WithValidBookingId_ShouldReturnPayment() {
        when(paymentService.getPaymentByBookingId(any(String.class)))
                .thenReturn(Mono.just(validResponse));

        webTestClient.get()
                .uri("/api/v1/payments/booking/{bookingId}", validRequest.getBookingId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(validResponse.getId())
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }
} 