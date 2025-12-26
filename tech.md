# EventPulse - Technology Stack

## Backend Framework
- **Spring Boot** (v3.2.0)
  - Spring MVC for web layer
  - Spring Security for authentication and authorization
  - Spring Data JPA for data access
  - Spring WebSocket for real-time updates
  - Spring Mail for email notifications

## Frontend Technologies
- **Thymeleaf** - Server-side template engine
- **Bootstrap** (v5.3.0) - CSS framework for responsive design
- **Bootstrap Icons** (v1.7.2) - Icon library
- **HTML5/CSS3** - Frontend markup and styling
- **JavaScript** - Client-side interactivity

## Database
- **MySQL** - Primary database
- **JPA/Hibernate** - ORM (Object-Relational Mapping)
- Connection pooling with built-in HikariCP

## Security
- Spring Security for authentication
- Password encoding with BCrypt
- Role-based access control (ADMIN, USER)
- Session management
- CSRF protection

## Email Service
- JavaMail API
- SMTP integration with Gmail
- HTML email templates

## Build Tools & Project Management
- **Maven** - Dependency management and build automation
- **Java** (v17) - Programming language
- Project Lombok - Boilerplate code reduction

## Development Tools
- **Spring Boot DevTools** - Development productivity tools
  - Live reload
  - Property defaults
  - Automatic restart

## Features & Integrations
- Event Management System
- Real-time updates using WebSocket
- User Authentication & Authorization
- RSVP System with Email Notifications
- Speaker Management
- Event Registration System
- Admin Dashboard
- Responsive UI

## Additional Libraries
- **Thymeleaf Extras** for Spring Security integration
- **MySQL Connector/J** for database connectivity

## Architecture
- MVC (Model-View-Controller) architecture
- Repository pattern for data access
- Service layer for business logic
- DTO pattern for data transfer
- RESTful design principles

## Project Structure
```
eventpulse/
├── src/main/java/
│   └── com/eventpulse/
│       ├── config/      (Configuration classes)
│       ├── controller/  (MVC Controllers)
│       ├── model/       (Entity classes)
│       ├── repository/  (Data access layer)
│       └── service/     (Business logic)
├── src/main/resources/
│   ├── static/         (Static resources)
│   ├── templates/      (Thymeleaf templates)
│   └── application.properties
└── pom.xml            (Project dependencies)
```