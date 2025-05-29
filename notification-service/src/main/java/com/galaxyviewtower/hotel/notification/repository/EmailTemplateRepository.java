package com.galaxyviewtower.hotel.notification.repository;

import com.galaxyviewtower.hotel.notification.model.EmailTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EmailTemplateRepository extends R2dbcRepository<EmailTemplate, String> {
    Mono<EmailTemplate> findByName(String name);
} 