package com.eventpulse.repository;

import com.eventpulse.model.SpeakerRsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeakerRsvpRepository extends JpaRepository<SpeakerRsvp, Long> {
}