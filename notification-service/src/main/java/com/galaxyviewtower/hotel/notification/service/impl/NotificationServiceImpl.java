package com.galaxyviewtower.hotel.notification.service.impl;

import com.galaxyviewtower.hotel.notification.dto.request.NotificationRequest;
import com.galaxyviewtower.hotel.notification.model.EmailTemplate;
import com.galaxyviewtower.hotel.notification.repository.EmailTemplateRepository;
import com.galaxyviewtower.hotel.notification.service.NotificationService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final freemarker.template.Configuration freemarkerConfig;
    private final EmailTemplateRepository templateRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> sendNotification(NotificationRequest request) {
        return templateRepository.findByName(request.getTemplateName())
                .flatMap(template -> {
                    try {
                        String processedSubject = processTemplate(template.getSubject(), request.getTemplateVariables());
                        String processedBody = processTemplate(template.getBody(), request.getTemplateVariables());
                        return sendEmail(request.getRecipientEmail(), processedSubject, processedBody);
                    } catch (Exception e) {
                        log.error("Error processing notification: {}", e.getMessage());
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public Mono<Void> processBookingConfirmation(String bookingId, String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(user -> {
                    NotificationRequest request = new NotificationRequest();
                    request.setTemplateName("booking_confirmation");
                    request.setRecipientEmail((String) user.get("email"));
                    request.setTemplateVariables(Map.of(
                        "bookingId", bookingId,
                        "userName", user.get("name")
                    ));
                    return sendNotification(request);
                });
    }

    @Override
    public Mono<Void> processPaymentConfirmation(String paymentId, String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(user -> {
                    NotificationRequest request = new NotificationRequest();
                    request.setTemplateName("payment_confirmation");
                    request.setRecipientEmail((String) user.get("email"));
                    request.setTemplateVariables(Map.of(
                        "paymentId", paymentId,
                        "userName", user.get("name")
                    ));
                    return sendNotification(request);
                });
    }

    @Override
    public Mono<Void> processCancellationNotice(String bookingId, String userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(user -> {
                    NotificationRequest request = new NotificationRequest();
                    request.setTemplateName("cancellation_notice");
                    request.setRecipientEmail((String) user.get("email"));
                    request.setTemplateVariables(Map.of(
                        "bookingId", bookingId,
                        "userName", user.get("name")
                    ));
                    return sendNotification(request);
                });
    }

    private String processTemplate(String templateContent, Map<String, Object> variables) throws IOException, TemplateException {
        Template template = new Template("dynamic", templateContent, freemarkerConfig);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, variables);
    }

    private Mono<Void> sendEmail(String to, String subject, String body) {
        return Mono.fromCallable(() -> {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            return null;
        });
    }
} 