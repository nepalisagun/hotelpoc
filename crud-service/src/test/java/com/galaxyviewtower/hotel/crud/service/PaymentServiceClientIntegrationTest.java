package com.galaxyviewtower.hotel.crud.service;

import com.galaxyviewtower.hotel.crud.config.WebClientConfig;
import com.galaxyviewtower.hotel.crud.dto.PaymentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceClientIntegrationTest {

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @MockBean
    private WebClient webClient;

    @MockBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @MockBean
    private WebClient.RequestBodySpec requestBodySpec;

    @MockBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestBodyUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void processPayment_ShouldProcessPayment() {
        // Arrange
        PaymentDTO paymentDTO = createSamplePayment(null);
        when(responseSpec.bodyToMono(PaymentDTO.class)).thenReturn(Mono.just(paymentDTO));

        // Act & Assert
        StepVerifier.create(paymentServiceClient.processPayment(paymentDTO))
            .expectNext(paymentDTO)
            .verifyComplete();
    }

    @Test
    void getPaymentStatus_ShouldReturnPaymentStatus() {
        // Arrange
        String paymentId = UUID.randomUUID().toString();
        PaymentDTO expectedPayment = createSamplePayment(paymentId);
        when(responseSpec.bodyToMono(PaymentDTO.class)).thenReturn(Mono.just(expectedPayment));

        // Act & Assert
        StepVerifier.create(paymentServiceClient.getPaymentStatus(paymentId))
            .expectNext(expectedPayment)
            .verifyComplete();
    }

    @Test
    void refundPayment_ShouldProcessRefund() {
        // Arrange
        String paymentId = UUID.randomUUID().toString();
        PaymentDTO refundPayment = createSamplePayment(paymentId);
        refundPayment.setStatus("REFUNDED");
        when(responseSpec.bodyToMono(PaymentDTO.class)).thenReturn(Mono.just(refundPayment));

        // Act & Assert
        StepVerifier.create(paymentServiceClient.refundPayment(paymentId))
            .expectNext(refundPayment)
            .verifyComplete();
    }

    @Test
    void processPayment_ShouldUseFallbackWhenServiceUnavailable() {
        // Arrange
        PaymentDTO paymentDTO = createSamplePayment(null);
        when(responseSpec.bodyToMono(PaymentDTO.class))
            .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        // Act & Assert
        StepVerifier.create(paymentServiceClient.processPayment(paymentDTO))
            .expectNextMatches(payment -> 
                payment.getStatus().equals("PENDING") &&
                payment.getMessage().contains("Payment service temporarily unavailable"))
            .verifyComplete();
    }

    private PaymentDTO createSamplePayment(String id) {
        return PaymentDTO.builder()
            .id(id != null ? id : UUID.randomUUID().toString())
            .bookingId(UUID.randomUUID().toString())
            .amount(new BigDecimal("299.99"))
            .currency("USD")
            .status("COMPLETED")
            .createdAt(LocalDateTime.now())
            .build();
    }
} 