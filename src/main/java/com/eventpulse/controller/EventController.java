package com.eventpulse.controller;

import com.eventpulse.model.Event;
import com.eventpulse.model.Speaker;
import com.eventpulse.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashSet;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "index";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        Event event = new Event();
        // Initialize with default values
        event.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
        event.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2).withMinute(0).withSecond(0));
        event.setSpeakers(new HashSet<>()); // Initialize empty speakers set
        model.addAttribute("event", event);
        model.addAttribute("isEdit", false);
        return "events/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(@ModelAttribute Event event, 
                            @RequestParam(required = false) String[] speakerNames,
                            @RequestParam(required = false) String[] speakerDescriptions) {
        // Validate and set default values if needed
        if (event.getStartTime() == null) {
            event.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
        }
        if (event.getEndTime() == null) {
            event.setEndTime(event.getStartTime().plusHours(2));
        }

        // Initialize speakers set if null
        if (event.getSpeakers() == null) {
            event.setSpeakers(new HashSet<>());
        }

        // Handle speakers
        if (speakerNames != null && speakerDescriptions != null && 
            speakerNames.length == speakerDescriptions.length) {
            for (int i = 0; i < speakerNames.length; i++) {
                if (speakerNames[i] != null && !speakerNames[i].trim().isEmpty()) {
                    Speaker speaker = new Speaker();
                    speaker.setName(speakerNames[i].trim());
                    speaker.setDescription(speakerDescriptions[i].trim());
                    speaker.setEvent(event);
                    event.getSpeakers().add(speaker);
                }
            }
        }
        
        Event savedEvent = eventService.createEvent(event);
        // Notify all clients about the new event
        messagingTemplate.convertAndSend("/topic/events", "new-event");
        return getAppropriateRedirect();
    }

    @GetMapping("/{id}")
    public String showEvent(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        // Ensure the event times are not null to prevent template errors
        if (event.getStartTime() == null) {
            event.setStartTime(LocalDateTime.now());
        }
        if (event.getEndTime() == null) {
            event.setEndTime(LocalDateTime.now().plusHours(1));
        }
        if (event.getSpeakers() == null) {
            event.setSpeakers(new HashSet<>());
        }
        model.addAttribute("event", event);
        model.addAttribute("isEdit", true);
        return "events/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateEvent(@PathVariable Long id, 
                            @ModelAttribute Event event,
                            @RequestParam(required = false) String[] speakerNames,
                            @RequestParam(required = false) String[] speakerDescriptions,
                            @RequestParam(required = false) Long[] speakerIds) {
        event.setId(id);
        if (event.getStartTime() == null) {
            event.setStartTime(LocalDateTime.now());
        }
        if (event.getEndTime() == null) {
            event.setEndTime(event.getStartTime().plusHours(1));
        }

        // Initialize speakers set if null
        if (event.getSpeakers() == null) {
            event.setSpeakers(new HashSet<>());
        } else {
            event.getSpeakers().clear(); // Clear existing speakers
        }

        // Handle speakers
        if (speakerNames != null && speakerDescriptions != null) {
            for (int i = 0; i < speakerNames.length; i++) {
                if (speakerNames[i] != null && !speakerNames[i].trim().isEmpty()) {
                    Speaker speaker = new Speaker();
                    // If we have an ID, this is an existing speaker
                    if (speakerIds != null && i < speakerIds.length) {
                        speaker.setId(speakerIds[i]);
                    }
                    speaker.setName(speakerNames[i].trim());
                    speaker.setDescription(speakerDescriptions[i].trim());
                    speaker.setEvent(event);
                    event.getSpeakers().add(speaker);
                }
            }
        }

        eventService.updateEvent(event);
        // Notify all clients about the updated event
        messagingTemplate.convertAndSend("/topic/events", "event-updated");
        return getAppropriateRedirect();
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        // Notify all clients about the deleted event
        messagingTemplate.convertAndSend("/topic/events", "event-deleted");
        return "redirect:/admin/dashboard";
    }
    
    private String getAppropriateRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/dashboard";
    }
}