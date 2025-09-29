# BookMe - Service Booking Platform

A comprehensive service booking platform built with Spring Boot that allows users to create services, manage bookings, and handle reviews with robust authentication and security features.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, authentication, email verification, and 2FA support
- **Service Management**: Create, update, and manage service listings with location and availability
- **Booking System**: Complete booking workflow with status management (pending, accepted, declined, cancelled)
- **Review System**: Rate and review services with validation
- **Location Services**: Full address management with city/state-based search
- **Real-time Notifications**: Email and SMS notifications for booking updates (COMING SOON)

### Security Features
- **JWT Authentication** with stateless session management
- **Two-Factor Authentication** via Twilio SMS (COMING SOON)
- **Email Verification** for account activation
- **Role-based Access Control** (User roles)
- **Password Encryption** with BCrypt
- **CORS Configuration** for cross-origin requests

### Technical Features
- **Database Migrations** with Flyway
- **Audit Trails** with JPA auditing
- **Input Validation** with Bean Validation
- **Exception Handling** with global exception handler
- **Multi-database Support** (PostgreSQL/MySQL)

## 🛠️ Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.5.5** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **Spring Mail** - Email notifications 
- **Spring WebSocket** - Real-time communication

### Database & Migration
- **PostgreSQL** (Primary)
- **MySQL** (Alternative)
- **Flyway** - Database migration tool

### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Twilio** - SMS verification service (COMING SOON)

### Build & Development
- **Maven** - Dependency management
- **Lombok** - Code generation
- **Spring Boot DevTools** - Development utilities

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **PostgreSQL 12+** or **MySQL 8.0+**
- **Git**

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/bookme.git
cd bookme
```

### 2. Database Setup
Create a database for the application:
```sql
-- PostgreSQL
CREATE DATABASE bookme;

-- MySQL
CREATE DATABASE bookme CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Environment Configuration
Create a `.env` file in the root directory:
```env
# Database Configuration
URL=jdbc:postgresql://localhost:5432/bookme
USERNAME=your_db_username
PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET_KEY=your_super_secret_jwt_key_here

# Email Configuration
SUPPORT_EMAIL=your_email@gmail.com
APP_PASSWORD=your_app_password

# Twilio Configuration (for SMS 2FA)
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_VERIFY_SERVICE_SID=your_twilio_verify_service_sid
```

### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "location": {
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "areaCode": "10001",
    "country": "USA"
  }
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Verify Account
```http
POST /auth/verify
Content-Type: application/json

{
  "email": "user@example.com",
  "verificationCode": "123456"
}
```

### Service Management

#### Create Service
```http
POST /service/create-service
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "handle": "my-service-handle",
  "serviceName": "Hair Salon",
  "description": "Professional hair services",
  "location": {
    "address": "456 Beauty Ave",
    "city": "New York",
    "state": "NY",
    "areaCode": "10002",
    "country": "USA"
  },
  "interval": 60,
  "open": "09:00",
  "close": "18:00"
}
```

#### Get Services by City
```http
GET /service/search/city/New York
```

### Booking Management

#### Create Booking
```http
POST /bookings/book
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "serviceId": 1,
  "date": "2024-01-15",
  "start": "10:00",
  "end": "11:00",
  "notes": "First time customer"
}
```

#### Accept Booking (Service Provider)
```http
PUT /bookings/{id}/service-accept
Authorization: Bearer <jwt_token>
```

#### Cancel Booking
```http
PUT /bookings/{id}/cancel-booking
Authorization: Bearer <jwt_token>
```

### Review System

#### Create Review
```http
POST /reviews/create
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "serviceId": 1,
  "rating": 5,
  "comment": "Excellent service!"
}
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/example/book/
│   │   ├── audit/              # JPA auditing configuration
│   │   ├── config/             # Application configuration
│   │   ├── controller/         # REST controllers
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── exception/         # Custom exception handling
│   │   ├── model/             # JPA entities
│   │   ├── repository/        # Data access layer
│   │   ├── response/          # Response DTOs
│   │   └── service/           # Business logic layer
│   └── resources/
│       ├── db/migration/      # Flyway database migrations
│       └── application.properties
└── test/                      # Test classes
```

## 🔒 Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt hashing with salt
- **Email Verification**: Required for account activation
- **Two-Factor Authentication**: SMS-based 2FA via Twilio (COMING SOON)
- **Role-based Access**: Different permissions for users
- **CORS Protection**: Configured for specific origins
- **Input Validation**: Comprehensive validation on all endpoints

## 🗄️ Database Schema

### Key Entities
- **Users**: User accounts with authentication and profile information
- **Services**: Service listings with location and availability
- **Bookings**: Appointment bookings with status tracking
- **Reviews**: User reviews and ratings for services
- **BookingChangeRequests**: Modification requests for existing bookings

### Relationships
- Users can have multiple Services
- Users can have multiple Bookings
- Services can have multiple Bookings and Reviews
- Bookings belong to one User and one Service

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/book-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables for Production
```env
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://your-db-host:5432/bookme
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET_KEY=your_production_secret_key
```

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 👨‍💻 Author

**Gabriel Pineda**
- GitHub: [@GabrielPineda808](https://github.com/GabrielPineda808)
- LinkedIn: [Gabriel Pineda](https://www.linkedin.com/in/gabriel-omar-pineda/)
- Email: gabepineda6@gmail.com

## 📈 Future Enhancements

- [ ] 2FA w/ Twilio
- [ ] Real-time chat between users and service providers
- [ ] Payment integration (Stripe/PayPal)
- [ ] Calendar integration (Google Calendar)
- [ ] Mobile application (React Native/Flutter)
- [ ] Advanced analytics dashboard
- [ ] Multi-language support (i18n)
- [ ] Push notifications
- [ ] Video consultation booking

---

**Built with ❤️ using Spring Boot and Java 21**