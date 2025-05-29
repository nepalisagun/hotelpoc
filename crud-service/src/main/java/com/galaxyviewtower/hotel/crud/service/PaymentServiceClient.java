package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.PaymentDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class PaymentServiceClient {

    private final WebClient webClient;
    private final WebClientConfig webClientConfig;

    public PaymentServiceClient(WebClient paymentServiceWebClient, WebClientConfig webClientConfig) {
        this.webClient = paymentServiceWebClient;
        this.webClientConfig = webClientConfig;
    }

    public Mono<PaymentDTO> processPayment(PaymentDTO paymentDTO) {
        return webClient.post()
            .uri("/api/payments/process")
            .bodyValue(paymentDTO)
            .retrieve()
            .bodyToMono(PaymentDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("paymentService"))
            .onErrorResume(CallNotPermittedException.class, e -> processFallbackPayment(paymentDTO));
    }

    public Mono<PaymentDTO> getPaymentStatus(String paymentId) {
        return webClient.get()
            .uri("/api/payments/{id}/status", paymentId)
            .retrieve()
            .bodyToMono(PaymentDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("paymentService"))
            .onErrorResume(CallNotPermittedException.class, e -> getFallbackPaymentStatus(paymentId));
    }

    public Mono<PaymentDTO> refundPayment(String paymentId) {
        return webClient.post()
            .uri("/api/payments/{id}/refund", paymentId)
            .retrieve()
            .bodyToMono(PaymentDTO.class)
            .transform(webClientConfig.applyResiliencePatterns("paymentService"))
            .onErrorResume(CallNotPermittedException.class, e -> refundFallbackPayment(paymentId));
    }

    @Cacheable(value = "paymentFallback", key = "#paymentDTO.id", unless = "#result == null")
    private Mono<PaymentDTO> processFallbackPayment(PaymentDTO paymentDTO) {
        return Mono.just(PaymentDTO.builder()
            .id("FALLBACK-" + System.currentTimeMillis())
            .bookingId(paymentDTO.getBookingId())
            .amount(paymentDTO.getAmount())
            .currency(paymentDTO.getCurrency())
            .status("PENDING")
            .message("Payment service temporarily unavailable. Payment will be processed when service is restored.")
            .createdAt(LocalDateTime.now())
            .build());
    }

    @Cacheable(value = "paymentFallback", key = "#paymentId", unless = "#result == null")
    private Mono<PaymentDTO> getFallbackPaymentStatus(String paymentId) {
        return Mono.just(PaymentDTO.builder()
            .id(paymentId)
            .status("UNKNOWN")
            .message("Payment service temporarily unavailable. Status cannot be determined.")
            .build());
    }

    @Cacheable(value = "paymentFallback", key = "#paymentId", unless = "#result == null")
    private Mono<PaymentDTO> refundFallbackPayment(String paymentId) {
        return Mono.just(PaymentDTO.builder()
            .id(paymentId)
            .status("REFUND_PENDING")
            .message("Payment service temporarily unavailable. Refund will be processed when service is restored.")
            .updatedAt(LocalDateTime.now())
            .build());
    }
} 