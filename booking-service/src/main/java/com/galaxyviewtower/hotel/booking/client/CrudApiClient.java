package com.galaxyviewtower.hotel.booking.client;

import com.galaxyviewtower.hotel.booking.model.Hotel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CrudApiClient {
    
    private final WebClient webClient;
    
    @Value("${crud.service.url:http://localhost:8080}")
    private String crudServiceUrl;

    public CrudApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(crudServiceUrl)
                .build();
    }

    public Mono<Hotel> getHotelById(String hotelId) {
        return webClient.get()
                .uri("/api/v1/hotels/{id}", hotelId)
                .retrieve()
                .bodyToMono(Hotel.class);
    }
}
