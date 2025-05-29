package com.galaxyviewtower.hotel.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Management System - User Service API")
                        .description("API documentation for the User Service of the Hotel Management System. " +
                                "This service handles user registration, authentication, and profile management. " +
                                "All endpoints except registration and login require JWT authentication.\n\n" +
                                "Rate Limiting:\n" +
                                "- Registration endpoint: 5 requests per minute\n" +
                                "- Login endpoint: 10 requests per minute\n" +
                                "- Other endpoints: 100 requests per minute\n\n" +
                                "Resilience Patterns:\n" +
                                "- Circuit Breaker: Opens after 5 failures, resets after 60 seconds\n" +
                                "- Retry: 3 attempts with exponential backoff\n" +
                                "- Bulkhead: Maximum 20 concurrent requests\n" +
                                "- Timeout: 5 seconds for all operations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Galaxy View Tower")
                                .email("support@galaxyviewtower.com")
                                .url("https://www.galaxyviewtower.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.galaxyviewtower.com/user-service")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for API authentication. " +
                                                "Include the token in the Authorization header as 'Bearer <token>'")));
    }
} 