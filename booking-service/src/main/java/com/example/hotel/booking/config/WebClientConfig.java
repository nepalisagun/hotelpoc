package com.example.hotel.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Value("${crud-service.url}")
  private String crudServiceUrl;

  @Bean
  public WebClient crudWebClient(WebClient.Builder builder) {
    return builder.baseUrl(crudServiceUrl).build();
  }
}
