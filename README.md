# 🎓 EventPulse

### Real-Time Academic Event Tracker

**EventPulse** is a Spring Boot application for managing academic events with real-time updates for students and organizers.

## 🚀 Key Features

* **Real-Time Dashboard:** Instant event updates via WebSockets.
* **Role-Based Access:** Secure login for Admins and Students.
* **Smart Notifications:** Automated email RSVPs.
* **Responsive UI:** Built with Thymeleaf & Bootstrap 5.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.2.0
* **Database:** MySQL, Spring Data JPA
* **Frontend:** Thymeleaf, Bootstrap, JavaScript

## ⚡ Quick Start

**1. Database Setup**
Create the database in MySQL:

```sql
CREATE DATABASE eventpulse;

```

**2. Configure**
Edit `src/main/resources/application.properties` with your credentials:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
# Add your mail password if testing emails
spring.mail.password=YOUR_APP_PASSWORD

```

**3. Run**

```bash
mvn spring-boot:run


