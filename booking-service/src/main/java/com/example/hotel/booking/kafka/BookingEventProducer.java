package com.example.hotel.booking.kafka;

import com.example.hotel.common.dto.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {
  private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
  private static final String TOPIC = "hotel-bookings";

  public void sendBookingCreatedEvent(BookingCreatedEvent event) {
    log.info("BOOKING: Sending BookingCreatedEvent to Kafka: {}", event.bookingId());
    try {
      kafkaTemplate.send(TOPIC, event.bookingId(), event);
    } catch (Exception e) {
      log.error("BOOKING: Failed to send event {} to Kafka", event.bookingId(), e);
    }
  }
}
