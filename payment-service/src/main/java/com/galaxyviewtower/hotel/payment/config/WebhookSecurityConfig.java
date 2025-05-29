package com.galaxyviewtower.hotel.payment.config;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Configuration
public class WebhookSecurityConfig {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    public Event constructEvent(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid webhook signature");
        }
    }
} 