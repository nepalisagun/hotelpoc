package com.galaxyviewtower.hotel.payment.service.impl;

import com.galaxyviewtower.hotel.payment.dto.request.PaymentRequest;
import com.galaxyviewtower.hotel.payment.dto.response.PaymentResponse;
import com.galaxyviewtower.hotel.payment.model.Payment;
import com.galaxyviewtower.hotel.payment.repository.PaymentRepository;
import com.galaxyviewtower.hotel.payment.service.PaymentService;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.RefundCreateParams;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${paypal.client.id}")
    private String paypalClientId;

    @Value("${paypal.client.secret}")
    private String paypalClientSecret;

    @Override
    @RateLimiter(name = "paymentService")
    @CircuitBreaker(name = "paymentService")
    @Retry(name = "paymentService")
    public Mono<PaymentResponse> initiatePayment(PaymentRequest request) {
        return paymentRepository.findByIdempotencyKey(request.getIdempotencyKey())
                .switchIfEmpty(Mono.defer(() -> {
                    Payment payment = new Payment();
                    payment.setId(UUID.randomUUID().toString());
                    payment.setBookingId(request.getBookingId());
                    payment.setUserId(request.getUserId());
                    payment.setAmount(request.getAmount());
                    payment.setCurrency(request.getCurrency());
                    payment.setStatus(Payment.PaymentStatus.PENDING);
                    payment.setPaymentMethodDetailsTokenized(request.getPaymentMethodDetailsTokenized());
                    payment.setPaymentGateway(request.getPaymentGateway());
                    payment.setIdempotencyKey(request.getIdempotencyKey());

                    return processPaymentWithGateway(request, payment);
                }))
                .map(PaymentResponse::fromPayment);
    }

    private Mono<Payment> processPaymentWithGateway(PaymentRequest request, Payment payment) {
        return switch (request.getPaymentGateway().toUpperCase()) {
            case "STRIPE" -> processStripePayment(request, payment);
            case "PAYPAL" -> processPayPalPayment(request, payment);
            default -> Mono.error(new IllegalArgumentException("Unsupported payment gateway: " + request.getPaymentGateway()));
        };
    }

    private Mono<Payment> processStripePayment(PaymentRequest request, Payment payment) {
        return Mono.fromCallable(() -> {
            Stripe.apiKey = stripeApiKey;
            Charge charge = createStripeCharge(request);
            payment.setPaymentGatewayTransactionId(charge.getId());
            return payment;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(paymentRepository::save);
    }

    private Mono<Payment> processPayPalPayment(PaymentRequest request, Payment payment) {
        return Mono.fromCallable(() -> {
            PayPalHttpClient client = new PayPalHttpClient(new com.paypal.core.SandboxEnvironment(paypalClientId, paypalClientSecret));
            Order order = createPayPalOrder(request);
            payment.setPaymentGatewayTransactionId(order.getId());
            return payment;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(paymentRepository::save);
    }

    private Order createPayPalOrder(PaymentRequest request) throws Exception {
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest();
        ordersCreateRequest.prefer("return=representation");
        ordersCreateRequest.requestBody(buildRequestBody(request));

        HttpResponse<Order> orderResponse = new PayPalHttpClient(
            new com.paypal.core.SandboxEnvironment(paypalClientId, paypalClientSecret))
            .execute(ordersCreateRequest);
        
        return orderResponse.result();
    }

    private OrderRequest buildRequestBody(PaymentRequest request) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
            .brandName("Hotel Booking")
            .landingPage("BILLING")
            .userAction("PAY_NOW");
        orderRequest.applicationContext(applicationContext);

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
            .amountWithBreakdown(new AmountWithBreakdown()
                .currencyCode(request.getCurrency())
                .value(request.getAmount().toString()));
        orderRequest.purchaseUnits(java.util.Collections.singletonList(purchaseUnitRequest));

        return orderRequest;
    }

    @Override
    @CircuitBreaker(name = "paymentService")
    public Mono<PaymentResponse> processPaymentCallback(String paymentId, String status) {
        return paymentRepository.findById(paymentId)
                .flatMap(payment -> {
                    payment.setStatus(Payment.PaymentStatus.valueOf(status));
                    return paymentRepository.save(payment)
                            .map(PaymentResponse::fromPayment);
                });
    }

    @Override
    @CircuitBreaker(name = "paymentService")
    @Retry(name = "paymentService")
    public Mono<PaymentResponse> refundPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .flatMap(payment -> {
                    if (payment.getPaymentGateway().equalsIgnoreCase("STRIPE")) {
                        return processStripeRefund(payment);
                    } else if (payment.getPaymentGateway().equalsIgnoreCase("PAYPAL")) {
                        return processPayPalRefund(payment);
                    }
                    return Mono.error(new IllegalArgumentException("Unsupported payment gateway for refund"));
                });
    }

    private Mono<PaymentResponse> processStripeRefund(Payment payment) {
        return Mono.fromCallable(() -> {
            Stripe.apiKey = stripeApiKey;
            return createStripeRefund(payment.getPaymentGatewayTransactionId());
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(refund -> {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            return paymentRepository.save(payment)
                    .map(PaymentResponse::fromPayment);
        });
    }

    private Mono<PaymentResponse> processPayPalRefund(Payment payment) {
        return Mono.fromCallable(() -> {
            PayPalHttpClient client = new PayPalHttpClient(
                new com.paypal.core.SandboxEnvironment(paypalClientId, paypalClientSecret));
            
            RefundRequest refundRequest = new RefundRequest();
            Money money = new Money()
                .currencyCode(payment.getCurrency())
                .value(payment.getAmount().toString());
            refundRequest.amount(money);

            HttpResponse<Refund> response = client.execute(new RefundsPostRequest()
                .requestBody(refundRequest)
                .pathVars(java.util.Collections.singletonMap("capture_id", payment.getPaymentGatewayTransactionId())));

            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            return payment;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(paymentRepository::save)
        .map(PaymentResponse::fromPayment);
    }

    @Override
    public Mono<PaymentResponse> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(PaymentResponse::fromPayment);
    }

    @Override
    public Mono<PaymentResponse> getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .map(PaymentResponse::fromPayment);
    }

    private Charge createStripeCharge(PaymentRequest request) throws StripeException {
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(request.getAmount().multiply(new java.math.BigDecimal("100")).longValue())
                .setCurrency(request.getCurrency().toLowerCase())
                .setSource(request.getPaymentMethodDetailsTokenized())
                .setDescription("Payment for booking: " + request.getBookingId())
                .build();

        return Charge.create(params);
    }

    private Refund createStripeRefund(String chargeId) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(chargeId)
                .build();

        return Refund.create(params);
    }
} 