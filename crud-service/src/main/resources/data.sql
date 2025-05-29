-- Insert sample hotels
INSERT INTO hotels (id, name, address, city, country, rating, total_rooms, price_per_night, phone_number, email, description, check_in_time, check_out_time, is_active)
VALUES 
('1', 'Grand Hotel', '123 Main Street', 'New York', 'USA', 4.5, 200, 299.99, '+1-555-0123', 'info@grandhotel.com', 'Luxury hotel in the heart of Manhattan', '14:00', '12:00', true),
('2', 'Seaside Resort', '456 Beach Road', 'Miami', 'USA', 4.8, 150, 399.99, '+1-555-0124', 'info@seasideresort.com', 'Beachfront resort with ocean views', '15:00', '11:00', true),
('3', 'Mountain View Lodge', '789 Hill Street', 'Denver', 'USA', 4.2, 100, 199.99, '+1-555-0125', 'info@mountainview.com', 'Cozy mountain retreat', '16:00', '10:00', true),
('4', 'Business Hotel', '321 Corporate Avenue', 'Chicago', 'USA', 4.0, 300, 249.99, '+1-555-0126', 'info@businesshotel.com', 'Modern business hotel in downtown', '13:00', '12:00', true),
('5', 'Boutique Inn', '654 Art Street', 'San Francisco', 'USA', 4.7, 50, 349.99, '+1-555-0127', 'info@boutiqueinn.com', 'Charming boutique hotel in arts district', '15:00', '11:00', true);

-- Insert sample room types
INSERT INTO room_types (id, hotel_id, name, description, capacity, base_price_per_night)
VALUES 
('1', '1', 'Deluxe King', 'Spacious room with king-size bed and city view', 2, 299.99),
('2', '1', 'Executive Suite', 'Luxury suite with separate living area', 4, 499.99),
('3', '2', 'Ocean View', 'Room with direct ocean view and balcony', 2, 399.99),
('4', '2', 'Beach Villa', 'Private villa with beach access', 6, 799.99),
('5', '3', 'Mountain Suite', 'Suite with mountain views and fireplace', 4, 299.99),
('6', '4', 'Business Suite', 'Suite with work area and meeting space', 2, 349.99),
('7', '5', 'Artist Loft', 'Unique loft-style room with art gallery access', 2, 399.99);

-- Insert sample rooms
INSERT INTO rooms (id, hotel_id, room_type_id, room_number, status)
VALUES 
('1', '1', '1', '101', 'AVAILABLE'),
('2', '1', '1', '102', 'AVAILABLE'),
('3', '1', '2', '201', 'AVAILABLE'),
('4', '2', '3', '301', 'AVAILABLE'),
('5', '2', '4', '401', 'AVAILABLE'),
('6', '3', '5', '501', 'AVAILABLE'),
('7', '4', '6', '601', 'AVAILABLE'),
('8', '5', '7', '701', 'AVAILABLE');

-- Insert sample amenities
INSERT INTO amenities (id, name, description, category)
VALUES 
('1', 'Swimming Pool', 'Outdoor swimming pool with temperature control', 'FACILITY'),
('2', 'Spa', 'Full-service spa with massage and treatments', 'WELLNESS'),
('3', 'Restaurant', 'Fine dining restaurant with international cuisine', 'DINING'),
('4', 'Gym', '24/7 fitness center with modern equipment', 'FACILITY'),
('5', 'Business Center', 'Business services and meeting rooms', 'BUSINESS'),
('6', 'Room Service', '24-hour room service', 'SERVICE'),
('7', 'Free WiFi', 'High-speed internet access', 'TECHNOLOGY'),
('8', 'Parking', 'Complimentary parking for guests', 'SERVICE');

-- Link amenities to hotels
INSERT INTO hotel_amenities (hotel_id, amenity_id)
VALUES 
('1', '1'), ('1', '2'), ('1', '3'), ('1', '4'), ('1', '5'), ('1', '6'), ('1', '7'), ('1', '8'),
('2', '1'), ('2', '2'), ('2', '3'), ('2', '4'), ('2', '6'), ('2', '7'), ('2', '8'),
('3', '1'), ('3', '2'), ('3', '3'), ('3', '4'), ('3', '6'), ('3', '7'), ('3', '8'),
('4', '3'), ('4', '4'), ('4', '5'), ('4', '6'), ('4', '7'), ('4', '8'),
('5', '2'), ('5', '3'), ('5', '6'), ('5', '7'), ('5', '8');

-- Link amenities to room types
INSERT INTO room_type_amenities (room_type_id, amenity_id)
VALUES 
('1', '7'), ('1', '6'),
('2', '7'), ('2', '6'), ('2', '5'),
('3', '7'), ('3', '6'),
('4', '7'), ('4', '6'), ('4', '1'),
('5', '7'), ('5', '6'),
('6', '7'), ('6', '6'), ('6', '5'),
('7', '7'), ('7', '6'); 