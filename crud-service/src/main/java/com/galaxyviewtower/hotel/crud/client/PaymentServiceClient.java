package com.galaxyviewtower.hotel.crud.client;

import com.galaxyviewtower.hotel.crud.dto.PaymentDTO;
import com.galaxyviewtower.hotel.crud.dto.PaymentRequestDTO;
import com.galaxyviewtower.hotel.crud.exception.ServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PaymentServiceClient {

    private final WebClient paymentServiceClient;

    @Autowired
    public PaymentServiceClient(WebClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "initiatePaymentFallback")
    @Retry(name = "paymentService", fallbackMethod = "initiatePaymentFallback")
    public Mono<PaymentDTO> initiatePayment(PaymentRequestDTO request) {
        return paymentServiceClient
                .post()
                .uri("/api/v1/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new ServiceException(
                                "Invalid payment request: " + e.getMessage(),
                                HttpStatus.BAD_REQUEST));
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "getPaymentFallback")
    @Retry(name = "paymentService", fallbackMethod = "getPaymentFallback")
    public Mono<PaymentDTO> getPaymentById(String paymentId) {
        return paymentServiceClient
                .get()
                .uri("/api/v1/payments/{id}", paymentId)
                .retrieve()
                .bodyToMono(PaymentDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "getPaymentByBookingFallback")
    @Retry(name = "paymentService", fallbackMethod = "getPaymentByBookingFallback")
    public Mono<PaymentDTO> getPaymentByBookingId(String bookingId) {
        return paymentServiceClient
                .get()
                .uri("/api/v1/payments/booking/{bookingId}", bookingId)
                .retrieve()
                .bodyToMono(PaymentDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                });
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "refundPaymentFallback")
    @Retry(name = "paymentService", fallbackMethod = "refundPaymentFallback")
    public Mono<PaymentDTO> refundPayment(String paymentId) {
        return paymentServiceClient
                .post()
                .uri("/api/v1/payments/{id}/refund", paymentId)
                .retrieve()
                .bodyToMono(PaymentDTO.class)
                .onErrorResume(ServiceException.class, e -> {
                    if (e.getStatus() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new ServiceException(
                                "Cannot refund payment: " + e.getMessage(),
                                HttpStatus.BAD_REQUEST));
                    }
                    return Mono.error(e);
                });
    }

    // Fallback methods
    private Mono<PaymentDTO> initiatePaymentFallback(PaymentRequestDTO request, Exception e) {
        return Mono.error(new ServiceException(
                "Failed to initiate payment: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }

    private Mono<PaymentDTO> getPaymentFallback(String paymentId, Exception e) {
        return Mono.error(new ServiceException(
                "Failed to get payment: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }

    private Mono<PaymentDTO> getPaymentByBookingFallback(String bookingId, Exception e) {
        return Mono.error(new ServiceException(
                "Failed to get payment by booking: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }

    private Mono<PaymentDTO> refundPaymentFallback(String paymentId, Exception e) {
        return Mono.error(new ServiceException(
                "Failed to refund payment: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e));
    }
} 