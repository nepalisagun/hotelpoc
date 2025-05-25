CREATE TABLE IF NOT EXISTS hotel (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    price_per_night DECIMAL(10, 2)
);

-- Pre-populate some data (optional, good for testing)
DELETE FROM hotel;
INSERT INTO hotel (id, name, city, price_per_night) VALUES ('h1', 'Grand Hyatt', 'New York', 350.75);
INSERT INTO hotel (id, name, city, price_per_night) VALUES ('h2', 'Hilton Downtown', 'Chicago', 280.00);