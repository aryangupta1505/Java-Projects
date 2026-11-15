### 🚆 Task 1: Online Reservation System
A desktop GUI application built with **Java Swing** and **JDBC (MySQL)** that simulates a railway ticket booking and cancellation portal[cite: 1].

#### Key Features
* **User Authentication:** Secure login screen backed by a MySQL `users` table that denies access for invalid credentials[cite: 1].
* **Ticket Reservation:** Interactive booking form capturing passenger details, class type, journey date, and source/destination stations[cite: 1].
* **Auto-Populated Train Details:** Automatically fetches and displays the corresponding train name when a valid train number is entered[cite: 1].
* **PNR Generation & Confirmation:** Saves validated bookings to the database using parameterized `PreparedStatement` queries, generates a unique PNR number, and displays a booking summary dialog[cite: 1].
* **Ticket Cancellation:** Retrieves full reservation details using a PNR number and deletes the record from the database upon user confirmation[cite: 1].
* **Input Validation:** Enforces non-empty fields, numeric-only train numbers, and strict `YYYY-MM-DD` date formatting[cite: 1].

#### Tech Stack
* **Language:** Java
* **GUI Framework:** Java Swing (`JFrame`, `JTabbedPane`, `CardLayout`)
* **Database:** MySQL (via MySQL Connector/J & JDBC)
