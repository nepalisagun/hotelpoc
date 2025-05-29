-- Drop existing tables if they exist
DROP TABLE IF EXISTS hotels;

-- Create hotels table
CREATE TABLE hotels (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    rating DECIMAL(3,1) NOT NULL
);