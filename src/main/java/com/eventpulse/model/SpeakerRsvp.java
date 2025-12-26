package com.eventpulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "speaker_rsvps")
public class SpeakerRsvp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "speaker_name", nullable = false)
    private String speakerName;

    @Column(name = "speaker_email", nullable = false)
    private String speakerEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "email_sent")
    private boolean emailSent;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}