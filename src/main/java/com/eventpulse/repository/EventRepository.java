package com.eventpulse.repository;

import com.eventpulse.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartTimeAfter(LocalDateTime from);
    List<Event> findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime from);
    List<Event> findByStartTimeBeforeAndEndTimeAfterOrderByStartTimeAsc(LocalDateTime now, LocalDateTime same);
    List<Event> findByEndTimeBeforeOrderByStartTimeDesc(LocalDateTime now);
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.speakers WHERE e.id = ?1")
    Optional<Event> findByIdWithSpeakers(Long id);
}
