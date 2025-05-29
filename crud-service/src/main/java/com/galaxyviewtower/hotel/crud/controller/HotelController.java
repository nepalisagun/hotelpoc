package com.galaxyviewtower.hotel.crud.controller;

import com.galaxyviewtower.hotel.crud.dto.HotelDTO;
import com.galaxyviewtower.hotel.crud.service.HotelService;
import com.galaxyviewtower.hotel.crud.validation.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel Management", description = "APIs for managing hotels")
@SecurityRequirement(name = "bearer-jwt")
public class HotelController {

    private final HotelService hotelService;
    private final ValidationService validationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new hotel", description = "Creates a new hotel with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Mono<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) {
        validationService.validateHotel(hotelDTO);
        return hotelService.createHotel(hotelDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieves a hotel by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel found"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public Mono<HotelDTO> getHotelById(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable String id) {
        return hotelService.getHotelById(id);
    }

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Retrieves all hotels with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public Flux<HotelDTO> getAllHotels(
            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by country")
            @RequestParam(required = false) String country,
            @Parameter(description = "Minimum rating")
            @RequestParam(required = false) Double minRating,
            @Parameter(description = "Maximum price per night")
            @RequestParam(required = false) Double maxPrice) {
        
        if (city != null) {
            city = validationService.sanitizeInput(city);
        }
        if (country != null) {
            country = validationService.sanitizeInput(country);
        }
        
        return hotelService.getAllHotels(city, country, minRating, maxPrice);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Updates an existing hotel's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Mono<HotelDTO> updateHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable String id,
            @RequestBody HotelDTO hotelDTO) {
        validationService.validateHotel(hotelDTO);
        return hotelService.updateHotel(id, hotelDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete hotel", description = "Deletes a hotel by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Mono<Void> deleteHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable String id) {
        return hotelService.deleteHotel(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle hotel status", description = "Toggles the active status of a hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Mono<HotelDTO> toggleHotelStatus(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable String id) {
        return hotelService.toggleHotelStatus(id);
    }
}
