-- Drop existing tables if they exist
DROP TABLE IF EXISTS booking_ledger;
DROP TABLE IF EXISTS booking_room_assignments;
DROP TABLE IF EXISTS bookings;

-- Create booking_ledger table
CREATE TABLE IF NOT EXISTS booking_ledger (
    id VARCHAR(255) PRIMARY KEY,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    room_id VARCHAR(255) NOT NULL,
    booking_id VARCHAR(255) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_dates CHECK (check_in_date < check_out_date),
    CONSTRAINT no_overlapping_bookings UNIQUE (room_id, check_in_date, check_out_date, status)
);

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_dates CHECK (check_in_date < check_out_date),
    CONSTRAINT valid_status_transition CHECK (
        (status = 'PENDING' AND room_id IS NULL) OR
        (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED') AND room_id IS NOT NULL)
    )
);

-- Create booking_room_assignments table
CREATE TABLE IF NOT EXISTS booking_room_assignments (
    id VARCHAR(255) PRIMARY KEY,
    booking_id VARCHAR(255) NOT NULL,
    room_id VARCHAR(255) NOT NULL,
    assigned_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT unique_room_assignment UNIQUE (room_id, booking_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_booking_ledger_hotel_room_type ON booking_ledger(hotel_id, room_type_id);
CREATE INDEX idx_booking_ledger_dates ON booking_ledger(check_in_date, check_out_date);
CREATE INDEX idx_booking_ledger_status ON booking_ledger(status);
CREATE INDEX idx_booking_ledger_booking ON booking_ledger(booking_id);

-- Create indexes
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_hotel_id ON bookings(hotel_id);
CREATE INDEX idx_bookings_room_type_id ON bookings(room_type_id);
CREATE INDEX idx_bookings_room_id ON bookings(room_id);
CREATE INDEX idx_bookings_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_booking_room_assignments_booking_id ON booking_room_assignments(booking_id);
CREATE INDEX idx_booking_room_assignments_room_id ON booking_room_assignments(room_id);

-- Create trigger for updating timestamp
CREATE TRIGGER update_booking_ledger_timestamp 
    BEFORE UPDATE ON booking_ledger 
    FOR EACH ROW 
    SET updated_at = CURRENT_TIMESTAMP;

CREATE TRIGGER update_bookings_timestamp
    BEFORE UPDATE ON bookings
    FOR EACH ROW
    CALL "com.galaxyviewtower.hotel.booking.config.TimestampTrigger"; 