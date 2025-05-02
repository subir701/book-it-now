
# BookItNow: Microservices-Based Online Ticket Booking System

**BookItNow** is a scalable, event-driven online ticket booking system built with Spring Boot Microservices. It supports concurrent bookings, secure authentication, and real-time event communication using RabbitMQ. Designed for both event organizers and attendees, the platform ensures high availability, modularity, and a clean developer experience.

---

## 🔧 Features

### ✅ **Microservices Architecture**
- **Modular Services:** User, Event, Seat, Booking, Payment, Notification.
- **Service Discovery:** Eureka for registering and discovering services.
- **API Gateway:** Centralized routing and JWT validation using Spring Cloud Gateway.

### 🔐 **Security**
- **JWT Authentication & Authorization:** 
  - Tokens are validated by the Gateway service.
  - Role-Based Access Control (RBAC) is enforced across services.

### 🧵 **Multithreading for Concurrency**
- Optimized for high traffic booking scenarios using synchronized blocks and thread-safe structures in Booking Service.

### 📬 **Event-Driven Communication**
- **RabbitMQ:** Used for inter-service communication (e.g., PaymentConfirmed → BookingUpdate).
- Ensures transactional consistency across distributed services.

### 🔍 **Swagger Integration**
- API documentation and token-based request testing available for each service.

### 💾 **Data Persistence**
- Each microservice persists its own data using **JPA + PostgreSQL**.

---

## 🧪 Technologies Used

| Category       | Tools/Tech                        |
|----------------|----------------------------------|
| **Backend**    | Spring Boot, Spring Cloud        |
| **Auth**       | JWT, Spring Security             |
| **Messaging**  | RabbitMQ                         |
| **Database**   | PostgreSQL                       |
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
├── notification-service/
├── common-lib/            # Shared models and utilities
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

---

## 🌟 Future Enhancements

| Feature                  | Description |
|--------------------------|-------------|
| **Email & SMS Notification** | Integration with Twilio/SendGrid |
| **Real Payment Gateway** | Integrate Stripe or Razorpay |
| **Caching Layer**        | Redis-based caching for seat/event data |
| **Circuit Breaker**      | Fault tolerance using Resilience4J |
| **Dockerization**        | Containerize all services |
| **Monitoring**           | Add Prometheus + Grafana for metrics |

---

## 🧑‍💻 Contributing
Contributions are welcome! Feel free to open issues or submit PRs.
