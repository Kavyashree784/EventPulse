package com.eventpulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"registeredUsers", "speakers"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "start_time", columnDefinition = "datetime")
    private LocalDateTime startTime;

    @Column(name = "end_time", columnDefinition = "datetime")
    private LocalDateTime endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "capacity", columnDefinition = "int")
    private Integer capacity;

    @Column(name = "is_accessible", columnDefinition = "tinyint(1)")
    private boolean accessible;

    @Column(name = "created_at", columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "datetime")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Speaker> speakers = new HashSet<>();

    @ManyToMany(mappedBy = "registeredEvents")
    private Set<User> registeredUsers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @PrePersist
    @PreUpdate
    public void updateStatus() {
        this.status = calculateStatus();
    }

    public EventStatus calculateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (endTime.isBefore(now)) {
            return EventStatus.COMPLETED;
        } else if (startTime.isAfter(now)) {
            return EventStatus.UPCOMING;
        } else {
            return EventStatus.ONGOING;
        }
    }

    public EventStatus getStatus() {
        if (this.status == null) {
            this.status = calculateStatus();
        }
        return this.status;
    }
}
