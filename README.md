# BookItNow: Online Ticket Booking System

**BookItNow** is a modern online ticket booking system designed to simplify the process of reserving tickets for events. Built with Spring Boot, it features robust APIs for managing users, events, sections, seats, and bookings. The application includes JWT-based authentication for secure access and aims to provide a seamless experience for both event organizers and attendees. Its modular architecture makes it scalable, easy to maintain, and ready for future enhancements like role-based access control.

---

## Features

- **Multithreading:** Efficient ticket booking for concurrent users.

  - **Implementation:** The system uses Java's multithreading to manage simultaneous ticket bookings. Conflicts are prevented by synchronized blocks and thread-safe collections.

- **Security:** Implements JWT-based authentication and authorization.

  - **Implementation:** JWT tokens are issued upon login and must be provided with each request in the Authorization header.

- **Swagger Integration:** The Swagger UI allows token authentication for testing endpoints.

- **User Management:** Includes roles for USER and ADMIN.

  - **Implementation:** Role-based access control (RBAC) ensures certain endpoints are restricted to admin roles.

- **Data Persistence:** Stores booking data securely.

  - **Implementation:** Booking details are stored using JPA in a relational database.

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
### Configure Environment Variables:
Update the application.properties file for your local database and JWT secret:

### properties

### Example
```bash
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=
spring.datasource.password=
```
### Build and Run:
### Build the application:
```bash
./gradlew build
```
Run the application:
```bash
./gradlew bootRun
```
The application will start at **http://localhost:8080**.
---
## Project Structure
```bash
BookItNow/
├── src
│   ├── main
│   │   ├── java       # Java source code
│   │   └── resources  # Configuration and static files
│   └── test           # Unit and integration tests
├── build.gradle       # Gradle build file
├── settings.gradle    # Project settings
├── gradlew            # Gradle wrapper (Linux/Mac)
├── gradlew.bat        # Gradle wrapper (Windows)
└── loggingfolder      # Application logs
```
---
## Using Postman and Swagger for Testing
### Postman:

- Import the API collection and set environment variables (e.g., base_url, auth_token).

- Test login, booking, and user management by sending requests to corresponding endpoints.

- For multithreading, use Postman’s Collection Runner to send multiple parallel requests.

### Swagger:

- Access the Swagger UI (usually available at http://localhost:8080/swagger-ui.html after starting the application).

- Use the Authorize button to add JWT tokens.

- Test all available endpoints directly from the Swagger interface.
---
## API Endpoints

### User Endpoints:
- `POST /bookitnow/v1/users/register`: Register a new user.
- `POST /bookitnow/v1/users/login`: Authenticate and get a JWT token.
- `GET /bookitnow/v1/users/details/{id}`: Retrieve user details.
- `PUT /bookitnow/v1/users/update/{id}`: Update user details.
- `DELETE /bookitnow/v1/users/delete/{id}`: Delete a user.

### Event Endpoints:
- `POST /bookitnow/v1/events`: Create a new event.
- `PUT /bookitnow/v1/events/update/{id}`: Update event details.
- `DELETE /bookitnow/v1/events/delete/{id}`: Delete an event.
- `GET /bookitnow/v1/events/venue/{venue}`: Get events by venue.
- `GET /bookitnow/v1/events/upcoming`: Fetch upcoming events.
- `GET /bookitnow/v1/events/available`: Fetch all available events.

### Section Endpoints:
- `POST /bookitnow/v1/sections/{eventId}/add`: Add a new section to an event.
- `PUT /bookitnow/v1/sections/{sectionId}/{capacity}`: Update section capacity.
- `GET /bookitnow/v1/sections/view/{sectionId}`: Fetch section details.
- `DELETE /bookitnow/v1/sections/delete/{sectionId}`: Delete a section.

### Seat Endpoints:
- `POST /bookitnow/v1/seats/{sectionId}`: Add a new seat to a section.

### Booking Endpoints:
- `POST /bookitnow/v1/bookings/new/{userId}`: Create a booking for a user.
- `GET /bookitnow/v1/bookings/user/{userId}`: Fetch bookings by user ID.
- `DELETE /bookitnow/v1/bookings/delete/{id}`: Delete a booking.
---
## Future Improvements
- **Transition to Microservices:**
  - Plan: Gradually decompose the monolithic architecture into smaller, independently deployable microservices.

  - Benefits: Improved scalability, better fault isolation, and easier updates for individual components.

  - Implementation Strategy: Break down booking, user management, notifications, and payment services into separate modules.

- **Enhanced Notification System:**
  - Plan: Expand the notification system to include email and SMS services.

  - Benefits: Users will have multiple channels to receive booking confirmations and updates.

  - Implementation Strategy: Integrate external notification services like Twilio or SendGrid.

- **Caching Mechanism:**
  - Plan: Implement caching to reduce database load and improve response times.

  - Benefits: Faster read performance for frequently accessed data.

  - Implementation Strategy: Use Redis or Ehcache for caching.

- **Email Notifications:**
  - Notify users upon successful bookings.

- **API Gateway Integration:**
  - Plan: Implement an API Gateway for better routing, load balancing, and security.

  - Benefits: Centralized control over all service interactions.

  - Implementation Strategy: Use tools like Spring Cloud Gateway or Zuul.
