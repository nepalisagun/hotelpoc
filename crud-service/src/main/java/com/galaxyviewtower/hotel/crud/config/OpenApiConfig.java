package com.galaxyviewtower.hotel.crud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Management System API")
                        .version("1.0.0")
                        .description("API documentation for the Hotel Management System's CRUD Service")
                        .contact(new Contact()
                                .name("Galaxy View Tower")
                                .email("support@galaxyviewtower.com")
                                .url("https://www.galaxyviewtower.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.galaxyviewtower.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.galaxyviewtower.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
} 