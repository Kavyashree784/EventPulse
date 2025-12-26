package com.eventpulse;

import com.eventpulse.model.User;
import com.eventpulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

@SpringBootApplication
public class EventpulseApplication {
    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        try {
            User existingAdmin = null;
            try {
                existingAdmin = userService.findByEmail("admin@gmail.com");
                // Update existing admin to ensure admin flag is set
                if (!existingAdmin.isAdmin()) {
                    existingAdmin.setAdmin(true);
                    userService.updateUser(existingAdmin);
                }
                System.out.println("Admin user verified and updated");
            } catch (UsernameNotFoundException e) {
                // Create new admin user
                User admin = new User();
                admin.setEmail("admin@gmail.com");
                admin.setPassword("1234");
                admin.setName("Admin");
                admin.setAdmin(true);
                userService.registerUser(admin);
                System.out.println("Admin user created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error during admin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Attempt to create the database if it doesn't exist before Spring Boot initializes JPA
        try (InputStream in = EventpulseApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                String url = p.getProperty("spring.datasource.url");
                String user = p.getProperty("spring.datasource.username");
                String pass = p.getProperty("spring.datasource.password");

                if (url != null && url.startsWith("jdbc:mysql://")) {
                    // Build an admin URL that points to server (no specific database)
                    int start = "jdbc:mysql://".length();
                    int slash = url.indexOf('/', start);
                    if (slash > 0) {
                        int q = url.indexOf('?', slash);
                        String params = q > 0 ? url.substring(q) : "";
                        String adminUrl = url.substring(0, slash + 1) + params; // ends with /?params or /

                        try (Connection c = DriverManager.getConnection(adminUrl, user, pass);
                             Statement s = c.createStatement()) {
                            s.executeUpdate("CREATE DATABASE IF NOT EXISTS eventpulse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                            System.out.println("Ensured database 'eventpulse' exists (or was created).");
                            // Now ensure the `events` table exists with a conservative schema compatible with most MySQL versions
                            try (Connection dbConn = DriverManager.getConnection(url.substring(0, slash + 1) + "eventpulse" + params, user, pass);
                                 Statement s2 = dbConn.createStatement()) {
                                // Drop and recreate tables to ensure correct schema
                String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(255) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "is_admin TINYINT(1) DEFAULT 0" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                s2.executeUpdate(createUsers);
                System.out.println("Users table created successfully");

                String createEvents = "CREATE TABLE IF NOT EXISTS events (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "description TEXT, " +
                    "start_time DATETIME, " +
                    "end_time DATETIME, " +
                    "location VARCHAR(255), " +
                    "capacity INT, " +
                    "sdg_tag VARCHAR(255), " +
                    "is_accessible TINYINT(1), " +
                    "created_at DATETIME, " +
                    "updated_at DATETIME" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                                s2.executeUpdate(createEvents);
                                System.out.println("Ensured table 'events' exists (or was created).");

                                String createSpeakers = "CREATE TABLE IF NOT EXISTS speakers (" +
                                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                    "name VARCHAR(255) NOT NULL, " +
                                    "description TEXT, " +
                                    "event_id BIGINT NOT NULL, " +
                                    "FOREIGN KEY (event_id) REFERENCES events(id)" +
                                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                                s2.executeUpdate(createSpeakers);
                                System.out.println("Ensured table 'speakers' exists (or was created).");
                            } catch (Exception ex2) {
                                System.err.println("Could not create 'events' table: " + ex2.getMessage());
                            }
                        } catch (Exception ex) {
                            // Do not fail startup here - log and continue; if DB isn't created, JPA will fail later.
                            System.err.println("Could not create database 'eventpulse': " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read application.properties before startup: " + e.getMessage());
        }

        SpringApplication.run(EventpulseApplication.class, args);
    }
}
