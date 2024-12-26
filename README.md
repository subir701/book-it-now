# BookItNow: Online Ticket Booking System

BookItNow is an online ticket booking application built with **Spring Boot**. It supports user registration, event management, seat selection, and booking operations. The application also includes **JWT-based authentication** for secure access.

---

## Features

### Core Functionality:
1. **User Management**
   - Register new users.
   - Authenticate users using JWT tokens.
   - Update and delete user details.
   - Fetch user details by ID.

2. **Event Management**
   - Create, update, and delete events.
   - Retrieve events by venue or get upcoming events.

3. **Section & Seat Management**
   - Add, update, and delete sections for an event.
   - Add seats to sections and manage seat availability.

4. **Booking Management**
   - Create bookings for users.
   - Fetch all bookings by user ID.
   - Delete bookings.

### Security:
- **JWT Authentication**:
  - Protects API endpoints with token-based authentication.
  - Ensures that only authenticated users can access specific features.

---

## Technologies Used

### Backend:
- **Spring Boot**: Framework for building RESTful APIs.
- **Spring Security**: Handles authentication and security.
- **JWT**: For secure, stateless authentication.

### Database:
- **MySQL Database**

### Validation:
- **Jakarta Bean Validation**: Ensures data integrity for entity fields.

---

## API Endpoints

### User Endpoints:
- `POST /api/v1/users/register`: Register a new user.
- `POST /api/v1/users/login`: Authenticate and get a JWT token.
- `GET /api/v1/users/{id}`: Retrieve user details.
- `PUT /api/v1/users/{id}`: Update user details.
- `DELETE /api/v1/users/{id}`: Delete a user.

### Event Endpoints:
- `POST /api/v1/events`: Create a new event.
- `PUT /api/v1/events/{id}`: Update event details.
- `DELETE /api/v1/events/{id}`: Delete an event.
- `GET /api/v1/events/venue/{venue}`: Get events by venue.
- `GET /api/v1/events/upcoming`: Fetch upcoming events.
- `GET /api/v1/events/available`: Fetch all available events.

### Section Endpoints:
- `POST /api/v1/sections/{eventId}/add`: Add a new section to an event.
- `PUT /api/v1/sections/{sectionId}/{capacity}`: Update section capacity.
- `GET /api/v1/sections/{sectionId}`: Fetch section details.
- `DELETE /api/v1/sections/delete/{sectionId}`: Delete a section.

### Seat Endpoints:
- `POST /api/v1/seats/{sectionId}`: Add a new seat to a section.

### Booking Endpoints:
- `POST /api/v1/bookings/{userId}`: Create a booking for a user.
- `GET /api/v1/bookings/user/{userId}`: Fetch bookings by user ID.
- `DELETE /api/v1/bookings/{id}`: Delete a booking.

---

## Prerequisites

### Tools:
- **Java 17** or higher.
- **Gradle** for dependency management.
- An IDE like IntelliJ IDEA, Eclipse, or VS Code.

### Setup:
1. Install **Java JDK 17+**.
2. Install **Gradle** if not already available.

---

## Getting Started

### Clone the Repository:
```bash
git clone https://github.com/your-username/bookitnow.git
cd bookitnow
```
## Configure Environment Variables:
Update the application.properties file for your local database and JWT secret:

###properties

### Example
```bash
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=
spring.datasource.password=
```
##Build and Run:
###Build the application:
```bash
./gradlew build
```
Run the application:
```bash
./gradlew bootRun
```
The application will start at ***http://localhost:8080***.
