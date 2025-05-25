package com.example.hotel.booking.service;

import com.example.hotel.booking.client.CrudApiClient;
import com.example.hotel.booking.kafka.BookingEventProducer;
import com.example.hotel.common.dto.BookingCreatedEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

  private final CrudApiClient crudApiClient;
  private final BookingEventProducer bookingEventProducer;

  public Mono<String> createBooking(String userId, String hotelId) {
    log.info("BOOKING: Attempting to create booking for user {} on hotel {}", userId, hotelId);
    // 1. Validate hotel exists (by calling CRUD service)
    return crudApiClient
        .getHotelById(hotelId)
        .flatMap(
            hotelDto -> {
              // 2. (Simulated) Create booking logic (check availability, payment, etc.)
              String bookingId = UUID.randomUUID().toString();
              log.info(
                  "BOOKING: Hotel {} found, proceeding with booking ID {}", hotelId, bookingId);

              // 3. Create Event
              BookingCreatedEvent event =
                  new BookingCreatedEvent(
                      bookingId,
                      userId,
                      hotelId,
                      LocalDate.now().plusDays(10), // Dummy dates
                      LocalDate.now().plusDays(12),
                      Instant.now());

              // 4. Publish event (fire-and-forget for demo, can be reactive)
              bookingEventProducer.sendBookingCreatedEvent(event);

              // 5. Return booking ID
              return Mono.just("Booking created: " + bookingId);
            })
        .onErrorResume(
            e -> {
              log.error(
                  "BOOKING: Failed to create booking for hotel {}: {}", hotelId, e.getMessage());
              return Mono.just("Booking failed: " + e.getMessage());
            });
  }
}
