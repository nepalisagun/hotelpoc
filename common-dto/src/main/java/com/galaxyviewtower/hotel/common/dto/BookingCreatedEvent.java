package com.galaxyviewtower.hotel.common.dto;

import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
  private String bookingId;
  private String userId;
  private String hotelId;
  private LocalDate checkInDate;
  private LocalDate checkOutDate;
  private Instant createdAt;
}
