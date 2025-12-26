package com.eventpulse.service;

import com.eventpulse.model.Event;
import com.eventpulse.model.EventStatus;
import com.eventpulse.model.User;
import com.eventpulse.repository.EventRepository;
import com.eventpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpeakerService speakerService;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    public Event createEvent(Event event) {
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
        event.setUpdatedAt(LocalDateTime.now());
        event.updateStatus(); // Ensure status is set
        
        // Handle speakers
        if (event.getSpeakers() != null) {
            event.getSpeakers().forEach(speaker -> {
                speaker.setEvent(event);
                // Ensure the speaker is properly initialized
                if (speaker.getDescription() == null) {
                    speaker.setDescription("");
                }
            });
        } else {
            event.setSpeakers(new HashSet<>());
        }
        
        Event saved = eventRepo.save(event);

        // Publish over WebSocket so clients can react in real-time (best-effort)
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/events", "new-event");
            } catch (Exception ex) {
                // swallow; real app should log
            }
        }

        return saved;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now());
    }

    public List<Event> getEventsByStatus(EventStatus status) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case UPCOMING:
                return eventRepo.findByStartTimeAfterOrderByStartTimeAsc(now);
            case ONGOING:
                return eventRepo.findByStartTimeBeforeAndEndTimeAfterOrderByStartTimeAsc(now, now);
            case COMPLETED:
                return eventRepo.findByEndTimeBeforeOrderByStartTimeDesc(now);
            default:
                throw new IllegalArgumentException("Invalid status");
        }
    }

    @Transactional
    public void registerUserForEvent(Long eventId, String userEmail) {
        Event event = getEventById(eventId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (event.getRegisteredUsers().size() >= event.getCapacity()) {
            throw new RuntimeException("Event is at full capacity");
        }

        event.getRegisteredUsers().add(user);
        user.getRegisteredEvents().add(event);
        
        eventRepo.save(event);
        userRepository.save(user);
    }

    @Transactional
    public void unregisterUserFromEvent(Long eventId, String userEmail) {
        Event event = getEventById(eventId);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        event.getRegisteredUsers().remove(user);
        user.getRegisteredEvents().remove(event);
        
        eventRepo.save(event);
        userRepository.save(user);
    }

    public Event getEventById(Long id) {
        return eventRepo.findByIdWithSpeakers(id).orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    public Event updateEvent(Event event) {
        Event existingEvent = getEventById(event.getId());
        
        // Update the fields
        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartTime(event.getStartTime());
        existingEvent.setEndTime(event.getEndTime());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setCapacity(event.getCapacity());
        existingEvent.setAccessible(event.isAccessible());
        existingEvent.setUpdatedAt(LocalDateTime.now());
        existingEvent.updateStatus(); // Ensure status is updated
        
        // Handle speakers
        // Clear existing speakers (they will be cascade deleted)
        existingEvent.getSpeakers().clear();
        
        // Add new or updated speakers
        if (event.getSpeakers() != null) {
            event.getSpeakers().forEach(speaker -> {
                speaker.setEvent(existingEvent);
                // Ensure the speaker description is not null
                if (speaker.getDescription() == null) {
                    speaker.setDescription("");
                }
                existingEvent.getSpeakers().add(speaker);
            });
        } else {
            event.setSpeakers(new HashSet<>());
        }
        
        // Preserve registered users relationship
        existingEvent.setRegisteredUsers(event.getRegisteredUsers());
        
        Event saved = eventRepo.save(existingEvent);

        // Notify clients about the update
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/events", "event-updated");
            } catch (Exception ex) {
                // swallow; real app should log
            }
        }

        return saved;
    }

    public void deleteEvent(Long id) {
        if (!eventRepo.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepo.deleteById(id);

        // Notify clients about the deletion
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/events", "event-deleted");
            } catch (Exception ex) {
                // swallow; real app should log
            }
        }
    }

    public long getTotalEventCount() {
        return eventRepo.count();
    }

    public long getActiveEventCount() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepo.findByStartTimeBeforeAndEndTimeAfterOrderByStartTimeAsc(now, now).size();
    }
}
