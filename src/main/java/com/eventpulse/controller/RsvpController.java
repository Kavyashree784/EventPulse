package com.eventpulse.controller;

import com.eventpulse.model.Event;
import com.eventpulse.model.SpeakerRsvp;
import com.eventpulse.repository.SpeakerRsvpRepository;
import com.eventpulse.service.EmailService;
import com.eventpulse.service.EventService;
import com.eventpulse.repository.RsvpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class RsvpController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SpeakerRsvpRepository rsvpRepository;

    @GetMapping("/rsvp")
    public String showRsvpForm(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/rsvp-form";
    }

    @PostMapping("/rsvp/send")
    public String sendRsvp(@RequestParam("speakerName") String speakerName,
                          @RequestParam("speakerEmail") String speakerEmail,
                          @RequestParam("eventId") Long eventId,
                          @RequestParam("startDate") String startDate,
                          @RequestParam("endDate") String endDate) {
        
        Event event = eventService.getEventById(eventId);
        
        SpeakerRsvp rsvp = new SpeakerRsvp();
        rsvp.setSpeakerName(speakerName);
        rsvp.setSpeakerEmail(speakerEmail);
        rsvp.setEvent(event);
        rsvp.setStartDate(LocalDateTime.parse(startDate));
        rsvp.setEndDate(LocalDateTime.parse(endDate));
        
        // Save RSVP
        rsvp = rsvpRepository.save(rsvp);
        
        // Send email
        try {
            emailService.sendRsvpEmail(rsvp);
            rsvp.setEmailSent(true);
            rsvpRepository.save(rsvp);
        } catch (Exception e) {
            // Log the error but don't stop the process
            System.err.println("Failed to send email: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
}