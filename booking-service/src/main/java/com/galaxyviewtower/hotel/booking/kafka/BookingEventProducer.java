package com.galaxyviewtower.hotel.booking.kafka;

import com.galaxyviewtower.hotel.common.dto.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {
  private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
  private static final String TOPIC = "booking-events";

  public void sendBookingCreatedEvent(BookingCreatedEvent event) {
    log.info("Sending booking created event: {}", event);
    kafkaTemplate.send(TOPIC, event.getBookingId(), event);
  }
}
