package com.galaxyviewtower.hotel.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Booking API")
                        .description("""
                            API for managing hotel bookings. This API provides endpoints for:
                            - Creating new bookings
                            - Retrieving booking details
                            - Checking hotel availability
                            - Managing booking status
                            
                            All endpoints require authentication using JWT Bearer token.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Galaxy View Tower")
                                .email("support@galaxyviewtower.com")
                                .url("https://www.galaxyviewtower.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Development server"),
                        new Server()
                                .url("https://api.galaxyviewtower.com")
                                .description("Production server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")));
    }
} 