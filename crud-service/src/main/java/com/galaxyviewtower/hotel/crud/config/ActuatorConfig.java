package com.galaxyviewtower.hotel.crud.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "crud-service")
            .commonTags("environment", System.getProperty("spring.profiles.active", "default"));
    }

    @Bean
    public HealthIndicator cacheHealthIndicator() {
        return () -> {
            Map<String, Object> details = new HashMap<>();
            details.put("status", "UP");
            details.put("timestamp", LocalDateTime.now().toString());
            return Status.UP.withDetails(details);
        };
    }

    @Bean
    public InfoContributor applicationInfoContributor(Environment env) {
        return (Info.Builder builder) -> {
            Map<String, Object> details = new HashMap<>();
            details.put("name", "Hotel CRUD Service");
            details.put("version", env.getProperty("app.version", "1.0.0"));
            details.put("spring.profiles.active", env.getProperty("spring.profiles.active", "default"));
            details.put("java.version", System.getProperty("java.version"));
            details.put("startup.time", LocalDateTime.now().toString());
            builder.withDetails(details);
        };
    }

    @Bean
    public WebEndpointProperties webEndpointProperties() {
        WebEndpointProperties properties = new WebEndpointProperties();
        properties.setBasePath("/actuator");
        properties.setExposure(new WebEndpointProperties.Exposure());
        properties.getExposure().getInclude().add("*");
        return properties;
    }

    @Bean
    public HealthEndpointProperties healthEndpointProperties() {
        HealthEndpointProperties properties = new HealthEndpointProperties();
        properties.setShowDetails(HealthEndpointProperties.Show.ALWAYS);
        properties.setShowComponents(HealthEndpointProperties.Show.ALWAYS);
        return properties;
    }

    @Bean
    public InfoEndpointProperties infoEndpointProperties() {
        InfoEndpointProperties properties = new InfoEndpointProperties();
        properties.setEnabled(true);
        return properties;
    }
} 