package com.galaxyviewtower.hotel.booking.service;

import com.galaxyviewtower.hotel.booking.model.Hotel;
import com.galaxyviewtower.hotel.booking.config.CrudServiceConfig;
import com.galaxyviewtower.hotel.booking.exception.HotelServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrudServiceClient {

    private final WebClient webClient;
    private final CrudServiceConfig crudServiceConfig;

    public Mono<Hotel> getHotelById(String hotelId) {
        if (hotelId == null || hotelId.trim().isEmpty()) {
            return Mono.error(new HotelServiceException(
                "Hotel ID cannot be null or empty",
                hotelId,
                "INVALID_HOTEL_ID"
            ));
        }

        log.info("Attempting to fetch hotel details from CRUD service - Hotel ID: {}", hotelId);
        
        return webClient
            .get()
            .uri(crudServiceConfig.getBaseUrl() + "/api/v1/hotels/" + hotelId)
            .retrieve()
            .onStatus(
                status -> status.equals(HttpStatus.NOT_FOUND),
                response -> response
                    .bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new HotelServiceException(
                        "Hotel not found with ID: " + hotelId,
                        hotelId,
                        "HOTEL_NOT_FOUND"
                    )))
            )
            .onStatus(
                status -> status.is4xxClientError(),
                response -> response
                    .bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new HotelServiceException(
                        "Invalid request for hotel ID: " + hotelId + ". Details: " + body,
                        hotelId,
                        "INVALID_REQUEST"
                    )))
            )
            .onStatus(
                status -> status.is5xxServerError(),
                response -> response
                    .bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new HotelServiceException(
                        "CRUD service is currently unavailable. Please try again later.",
                        hotelId,
                        "SERVICE_UNAVAILABLE"
                    )))
            )
            .bodyToMono(Hotel.class)
            .doOnSuccess(hotel -> 
                log.info("Successfully retrieved hotel details - Hotel ID: {}, Name: {}", 
                    hotel.getId(), hotel.getName()))
            .doOnError(error -> {
                log.error("Error fetching hotel {} from CRUD service: {} | Stack trace: {}", 
                    hotelId, error.getMessage(), error);
                if (!(error instanceof HotelServiceException)) {
                    throw new HotelServiceException(
                        "Failed to fetch hotel details. Please try again later.",
                        hotelId,
                        "SERVICE_ERROR",
                        error
                    );
                }
                throw (HotelServiceException) error;
            });
    }
} 