# Booking Service

The Booking Service is responsible for managing hotel room bookings, handling the booking lifecycle, and ensuring room availability.

## Features

- Room availability checking
- Booking creation and management
- Booking status tracking
- Room assignment management
- Booking history and reporting

## Database Schema

### Bookings Table

```sql
CREATE TABLE bookings (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    room_id VARCHAR(255),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL CHECK (number_of_guests > 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0.0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Key constraints:

- `valid_dates`: Ensures check-in date is before check-out date
- `valid_status_transition`: Ensures room assignment aligns with booking status
- `number_of_guests`: Must be greater than 0
- `total_price`: Must be non-negative

### Booking Room Assignments Table

```sql
CREATE TABLE booking_room_assignments (
    id VARCHAR(255) PRIMARY KEY,
    booking_id VARCHAR(255) NOT NULL,
    room_id VARCHAR(255) NOT NULL,
    assigned_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT unique_room_assignment UNIQUE (room_id, booking_id)
);
```

Key constraints:

- Foreign key relationship with bookings table
- Unique constraint on room_id and booking_id combination
- Cascade delete when booking is deleted

### Indexes

The following indexes are created for optimal query performance:

Bookings table:

- `idx_bookings_user_id`: For user booking history queries
- `idx_bookings_hotel_id`: For hotel-specific booking queries
- `idx_bookings_room_type_id`: For room type availability queries
- `idx_bookings_room_id`: For specific room booking queries
- `idx_bookings_dates`: For date range queries
- `idx_bookings_status`: For status-based queries

Booking Room Assignments table:

- `idx_booking_room_assignments_booking_id`: For booking-specific assignments
- `idx_booking_room_assignments_room_id`: For room-specific assignments

## API Endpoints

### Booking Management

- `POST /api/bookings`: Create a new booking
- `GET /api/bookings/{id}`: Get booking details
- `PUT /api/bookings/{id}`: Update booking
- `DELETE /api/bookings/{id}`: Cancel booking
- `GET /api/bookings/user/{userId}`: Get user's bookings
- `GET /api/bookings/hotel/{hotelId}`: Get hotel's bookings

### Room Assignment

- `POST /api/bookings/{id}/rooms`: Assign rooms to booking
- `GET /api/bookings/{id}/rooms`: Get assigned rooms
- `DELETE /api/bookings/{id}/rooms/{roomId}`: Remove room assignment

## Dependencies

- Spring Boot 3.x
- Spring WebFlux
- R2DBC
- PostgreSQL
- Spring Security
- Spring Cloud
- Resilience4j

## Configuration

The service can be configured using the following properties:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/hotel_booking
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  cloud:
    discovery:
      enabled: true
    config:
      enabled: true

resilience4j:
  ratelimiter:
    instances:
      booking:
        limitForPeriod: 100
        limitRefreshPeriod: 1s
        timeoutDuration: 0s
```

## Running the Service

1. Ensure PostgreSQL is running
2. Set up environment variables
3. Run the service:
   ```bash
   ./gradlew :booking-service:bootRun
   ```

## Testing

Run the test suite:

```bash
./gradlew :booking-service:test
```
