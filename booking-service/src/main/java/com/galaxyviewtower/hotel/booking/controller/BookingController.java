package com.galaxyviewtower.hotel.booking.controller;

import com.galaxyviewtower.hotel.booking.dto.ApiResponse;
import com.galaxyviewtower.hotel.booking.dto.BookingRequestDto;
import com.galaxyviewtower.hotel.booking.dto.BookingResponseDto;
import com.galaxyviewtower.hotel.booking.exception.BookingException;
import com.galaxyviewtower.hotel.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.headers.Header;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Booking", description = "Hotel booking management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    private final BookingService bookingService;

    @Operation(
        summary = "Create a new booking",
        description = """
            Creates a new hotel booking with the provided details.
            
            Rate limiting:
            - 100 requests per minute per user
            - 1000 requests per hour per user
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Booking created successfully",
                content = @Content(schema = @Schema(implementation = BookingResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid booking request",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing authentication token",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "429",
                description = "Too Many Requests - Rate limit exceeded",
                headers = {
                    @Header(name = "X-RateLimit-Limit", description = "Rate limit per minute"),
                    @Header(name = "X-RateLimit-Remaining", description = "Remaining requests in current window"),
                    @Header(name = "X-RateLimit-Reset", description = "Time when the rate limit resets")
                }
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
        }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto request) {
        log.info("Received booking request: {}", request);
        return bookingService.createBooking(request);
    }

    @Operation(
        summary = "Get booking by ID",
        description = """
            Retrieves a booking by its unique identifier.
            
            Rate limiting:
            - 200 requests per minute per user
            - 2000 requests per hour per user
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Booking found",
                content = @Content(schema = @Schema(implementation = BookingResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing authentication token",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Booking not found",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "429",
                description = "Too Many Requests - Rate limit exceeded",
                headers = {
                    @Header(name = "X-RateLimit-Limit", description = "Rate limit per minute"),
                    @Header(name = "X-RateLimit-Remaining", description = "Remaining requests in current window"),
                    @Header(name = "X-RateLimit-Reset", description = "Time when the rate limit resets")
                }
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
        }
    )
    @GetMapping("/{bookingId}")
    public Mono<ResponseEntity<ApiResponse<BookingResponseDto>>> getBooking(
            @Parameter(description = "ID of the booking to retrieve", required = true)
            @PathVariable String bookingId) {
        log.info("Received request to get booking: {}", bookingId);
        return bookingService.getBooking(bookingId)
                .map(booking -> ResponseEntity.ok(ApiResponse.success(booking)))
                .onErrorResume(BookingException.class, e -> {
                    log.error("Error retrieving booking {}: {}", bookingId, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(e.getMessage(), e.getErrorCode())));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Unexpected error retrieving booking", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error("An unexpected error occurred")));
                });
    }

    @Operation(
        summary = "Check hotel availability",
        description = """
            Checks if a hotel is available for the given dates and number of rooms.
            
            Rate limiting:
            - 300 requests per minute per user
            - 3000 requests per hour per user
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Availability check completed",
                content = @Content(schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing authentication token",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                responseCode = "429",
                description = "Too Many Requests - Rate limit exceeded",
                headers = {
                    @Header(name = "X-RateLimit-Limit", description = "Rate limit per minute"),
                    @Header(name = "X-RateLimit-Remaining", description = "Remaining requests in current window"),
                    @Header(name = "X-RateLimit-Reset", description = "Time when the rate limit resets")
                }
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
        }
    )
    @GetMapping("/availability")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> checkAvailability(
            @Parameter(description = "ID of the hotel to check", required = true)
            @RequestParam String hotelId,
            @Parameter(description = "Check-in date (yyyy-MM-dd)", required = true)
            @RequestParam LocalDate checkInDate,
            @Parameter(description = "Check-out date (yyyy-MM-dd)", required = true)
            @RequestParam LocalDate checkOutDate,
            @Parameter(description = "Number of rooms needed", required = false)
            @RequestParam(defaultValue = "1") int rooms) {
        log.info("Checking availability for hotel: {}, dates: {} to {}, rooms: {}", 
                hotelId, checkInDate, checkOutDate, rooms);
        return bookingService.checkAvailability(hotelId, checkInDate, checkOutDate, rooms)
                .map(available -> ResponseEntity.ok(ApiResponse.success(available)))
                .onErrorResume(BookingException.class, e -> {
                    log.error("Error checking availability: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error(e.getMessage(), e.getErrorCode())));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Unexpected error checking availability", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponse.error("An unexpected error occurred")));
                });
    }

    @PutMapping("/{id}")
    public Mono<BookingResponseDto> updateBooking(@PathVariable String id, @Valid @RequestBody BookingRequestDto bookingDTO) {
        return bookingService.updateBooking(id, bookingDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBooking(@PathVariable String id) {
        return bookingService.deleteBooking(id);
    }

    @GetMapping("/user/{userId}")
    public Flux<BookingResponseDto> getBookingsByUserId(@PathVariable String userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @GetMapping("/hotel/{hotelId}")
    public Flux<BookingResponseDto> getBookingsByHotelId(@PathVariable String hotelId) {
        return bookingService.getBookingsByHotelId(hotelId);
    }

    @PostMapping("/{id}/cancel")
    public Mono<BookingResponseDto> cancelBooking(@PathVariable String id) {
        return bookingService.cancelBooking(id);
    }

    @PostMapping("/{id}/confirm")
    public Mono<BookingResponseDto> confirmBooking(@PathVariable String id) {
        return bookingService.confirmBooking(id);
    }
} 