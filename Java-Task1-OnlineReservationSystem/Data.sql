CREATE DATABASE IF NOT EXISTS reservation_db;
USE reservation_db;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    pnr VARCHAR(20) PRIMARY KEY,
    passenger_name VARCHAR(100) NOT NULL,
    train_number VARCHAR(20) NOT NULL,
    train_name VARCHAR(100) NOT NULL,
    class_type VARCHAR(50) NOT NULL,
    journey_date DATE NOT NULL,
    source_stn VARCHAR(100) NOT NULL,
    dest_stn VARCHAR(100) NOT NULL
);

INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123');