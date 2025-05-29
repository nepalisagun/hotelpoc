-- Drop existing tables if they exist
DROP TABLE IF EXISTS room_availability;
DROP TABLE IF EXISTS hotel_amenities;
DROP TABLE IF EXISTS room_type_amenities;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS room_types;
DROP TABLE IF EXISTS amenities;
DROP TABLE IF EXISTS hotels;

-- Create hotels table with refined structure
CREATE TABLE IF NOT EXISTS hotels (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    rating DECIMAL(3,1) NOT NULL CHECK (rating >= 0.0 AND rating <= 5.0),
    total_rooms INT NOT NULL CHECK (total_rooms > 0 AND total_rooms <= 10000),
    price_per_night DECIMAL(10,2) NOT NULL CHECK (price_per_night >= 0.0 AND price_per_night <= 100000.0),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    description TEXT,
    check_in_time VARCHAR(5) NOT NULL,
    check_out_time VARCHAR(5) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_check_in_time CHECK (check_in_time ~ '^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$'),
    CONSTRAINT valid_check_out_time CHECK (check_out_time ~ '^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$'),
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create amenities table
CREATE TABLE IF NOT EXISTS amenities (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create room_types table
CREATE TABLE IF NOT EXISTS room_types (
    id VARCHAR(255) PRIMARY KEY,
    hotel_id VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    capacity INT NOT NULL CHECK (capacity > 0 AND capacity <= 10),
    base_price_per_night DECIMAL(10,2) NOT NULL CHECK (base_price_per_night >= 0.0 AND base_price_per_night <= 100000.0),
    size_sqm DECIMAL(6,2) CHECK (size_sqm > 0),
    bed_type VARCHAR(50),
    view_type VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT unique_room_type_name_per_hotel UNIQUE (hotel_id, name)
);

-- Create rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id VARCHAR(255) PRIMARY KEY,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    floor_number INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'CLEANING')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE,
    CONSTRAINT unique_room_number_per_hotel UNIQUE (hotel_id, room_number)
);

-- Create hotel_amenities junction table
CREATE TABLE IF NOT EXISTS hotel_amenities (
    hotel_id VARCHAR(255) NOT NULL,
    amenity_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hotel_id, amenity_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
);

-- Create room_type_amenities junction table
CREATE TABLE IF NOT EXISTS room_type_amenities (
    room_type_id VARCHAR(255) NOT NULL,
    amenity_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_type_id, amenity_id),
    FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
);

-- Create room_availability table to track room bookings
CREATE TABLE IF NOT EXISTS room_availability (
    id VARCHAR(255) PRIMARY KEY,
    room_id VARCHAR(255) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    booking_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT valid_dates CHECK (check_in_date < check_out_date),
    CONSTRAINT no_overlapping_bookings UNIQUE (room_id, check_in_date, check_out_date)
);

-- Create indexes for better query performance
CREATE INDEX idx_hotels_city_country ON hotels(city, country);
CREATE INDEX idx_hotels_rating ON hotels(rating);
CREATE INDEX idx_hotels_price ON hotels(price_per_night);
CREATE INDEX idx_hotels_active ON hotels(is_active);

CREATE INDEX idx_room_types_hotel ON room_types(hotel_id);
CREATE INDEX idx_room_types_price ON room_types(base_price_per_night);
CREATE INDEX idx_room_types_active ON room_types(is_active);

CREATE INDEX idx_rooms_hotel ON rooms(hotel_id);
CREATE INDEX idx_rooms_room_type ON rooms(room_type_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_active ON rooms(is_active);

CREATE INDEX idx_amenities_active ON amenities(is_active);

-- Create indexes for room availability
CREATE INDEX idx_room_availability_room ON room_availability(room_id);
CREATE INDEX idx_room_availability_dates ON room_availability(check_in_date, check_out_date);
CREATE INDEX idx_room_availability_booking ON room_availability(booking_id);

CREATE INDEX idx_hotel_amenities_hotel ON hotel_amenities(hotel_id);
CREATE INDEX idx_hotel_amenities_amenity ON hotel_amenities(amenity_id);
CREATE INDEX idx_room_type_amenities_room_type ON room_type_amenities(room_type_id);
CREATE INDEX idx_room_type_amenities_amenity ON room_type_amenities(amenity_id);

-- Create triggers for updating timestamps
CREATE TRIGGER update_hotels_timestamp 
    BEFORE UPDATE ON hotels 
    FOR EACH ROW 
    SET updated_at = CURRENT_TIMESTAMP;

CREATE TRIGGER update_amenities_timestamp
    BEFORE UPDATE ON amenities
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_room_types_timestamp
    BEFORE UPDATE ON room_types
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_rooms_timestamp
    BEFORE UPDATE ON rooms
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_room_availability_timestamp 
    BEFORE UPDATE ON room_availability 
    FOR EACH ROW 
    SET updated_at = CURRENT_TIMESTAMP;