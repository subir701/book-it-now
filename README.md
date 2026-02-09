
# BookItNow: Microservices-Based Online Ticket Booking System

**BookItNow** is a scalable, event-driven online ticket booking system built with Spring Boot Microservices. It supports concurrent bookings, secure authentication, and real-time event communication using RabbitMQ. Designed for both event organizers and attendees, the platform ensures high availability, modularity, and a clean developer experience.

---

## 🔧 Features

### ✅ **Microservices Architecture**
- **Modular Services:** User, Event, Seat, Booking, Payment, Notification.
- **Service Discovery:** Eureka for registering and discovering services.
- **API Gateway:** Centralized routing and JWT validation using Spring Cloud Gateway.
- **Fault Tolerance:** Implementing Resilience4j (Cricuit Breaker & Retry) to handle service inter-dependencies and prevent cascading failure.

### 🔐 **Security**
- **JWT Authentication & Authorization:** 
  - Tokens are validated by the Gateway service.
  - Role-Based Access Control (RBAC) is enforced across services.
- **Secure Communication:**
-  Pass-through of security context ensures authenticated user IDs are consistent across service boundaries.

### 🧵 **Concurrency & Distributed Locking**
- **Redisson (Redis) Locks:** Implements distributed locking for seat selection to prevent race conditions in high-concurrency environments.
- **Optimistic Locking:** Utilizes JPA @Version as a secondary defense layer for data integrity.
- **Thread Safety:** Optimized Booking Service using thread-safe structures to manage high-volume transactional throughput.

### 📨 **Messaging Architecture (Choreography Saga)**
- **Hybrid Messaging:** Utilizes both Kafka and RabbitMQ to balance high throughput with complex transactional routing.
- **Kafka:** Used for high-volume messaging (e.g., seat status updates)
  - Topic: reservation.create, reservation.cancel, etc.
- **RabbitMQ Queues:**
  - seat.status.update.queue: Updates seat status after booking
  - payment.confirmation.queue: Confirms payment and triggers booking
  - notification.queue: Sends booking confirmation notifications
- Ensures transactional consistency across distributed services.
- **Idempotency:** Implementation of paymentId checks in the Booking Service to ensure "Exactly-Once" processing of payment messages.

### 🔍 **Swagger Integration**
- API documentation and token-based request testing available for each service.

### 💾 **Data Persistence**
- **Polyglot Persistence:** Each service manages its own PostgreSQL instance.
- **JSONB Optimization:** Uses Postgres JSONB columns for flexible seat detail storage, mapped via JPA Attribute Converters.
- **Performance Tuning:** Optimized data retrieval using JOIN FETCH and DTO Projections to eliminate the N+1 query problem.

---

## 🧪 Technologies Used

| Category       | Tools/Tech                       |
|----------------|----------------------------------|
| **Backend**    | Spring Boot, Spring Cloud        |
| **Auth**       | JWT, Spring Security             |
| **Messaging**  | RabbitMQ, Kafka                  |
| **Database**   | PostgreSQL, Redis                |
| **Discovery**  | Eureka                           |
| **Gateway**    | Spring Cloud Gateway             |
| **Build Tool** | Gradle                           |
| **Docs**       | Swagger                          |
| **Dev Tools**  | Lombok, Slf4j                    |

---

## 🚀 Getting Started

### ✅ Prerequisites
- Java 17+
- Gradle
- RabbitMQ Server (locally or via Docker)
- Kafka and Zookeeper
- PostgreSQL
- IntelliJ / Eclipse / VS Code

### 🔧 Setup Instructions

1. **Clone the Repository**
```bash
git clone https://github.com/your-username/bookitnow.git
cd bookitnow
```

2. **Configure Environment**
Update `application.properties` or `.yml` in each microservice:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookitnow_users
spring.datasource.username=your_db_user
spring.datasource.password=your_password

jwt.secret=your_jwt_secret
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
spring.rabbitmq.host=localhost
```

3. **Start Eureka and RabbitMQ**

4. **Run Services**
```bash
./gradlew bootRun
```
Run each service individually, or use a script/docker-compose if configured.

---

## 📁 Project Structure

```
bookitnow/
├── api-gateway/
├── discovery-service/
├── user-service/
├── event-service/
├── seat-service/
├── booking-service/
├── payment-service/
├── common-lib/            # Shared models and utilities
├── loggingfolder/
│   └── *.log
└── build.gradle
```

---

## 🔄 Key API Endpoints

### User Service
- `POST /users/register`
- `POST /users/login`
- `GET /users/details/{id}`

### Event Service
- `POST /events`
- `GET /events/upcoming`

### Section & Seat Service
- `POST /sections/{eventId}/add`
- `PUT /sections/{sectionId}/{capacity}`
- `GET /sections/view/{id}`

### Booking Service
- `POST /bookings/new/{userId}`
- `GET /bookings/user/{userId}`

### Payment Service (Mocked)
- `POST /payments/initiate/{bookingId}`

---

## 🧪 Testing

### Swagger
- Available at:  
  - `http://localhost:8081/swagger-ui.html` (Gateway)
  - `http://localhost:808x/swagger-ui.html` (Individual services)

### Postman
- Use collection runner for stress testing booking concurrency.
- Pass JWT tokens in headers for protected routes.

### 📨 Messaging Architecture
**Kafka**
- Used for high-volume messaging (e.g., seat status updates)
- **Topic:** reservation.create, reservation.cancel, etc.

**RabbitMQ**
- **Queues:**
- **seat.status.update.queue:** Updates seat status after booking
- **payment.confirmation.queue:** Confirms payment and triggers booking
- **notification.queue:** Sends booking confirmation notifications

---

## 🌟 Future Enhancements

| Feature                  | Description |
|--------------------------|-------------|
| **Email & SMS Notification** | Integration with Twilio/SendGrid |
| **Real Payment Gateway** | Integrate Stripe or Razorpay |
| **Dockerization**        | Containerize all services |
| **Monitoring**           | Add Prometheus + Grafana for metrics |
| **Advanced Security**    | Implement OAuth2 with Keycloak |
| **Database Sharding**    | Explore Postgres partitioning or Read Replica |

---
