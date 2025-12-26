package com.eventpulse.controller;

import com.eventpulse.model.Event;
import com.eventpulse.model.EventStatus;
import com.eventpulse.service.EventService;
import com.eventpulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get events by status
        model.addAttribute("upcomingEvents", eventService.getEventsByStatus(EventStatus.UPCOMING));
        model.addAttribute("activeEvents", eventService.getEventsByStatus(EventStatus.ONGOING));
        model.addAttribute("completedEvents", eventService.getEventsByStatus(EventStatus.COMPLETED));
        
        // Add statistics
        long totalEvents = eventService.getTotalEventCount();
        long totalUsers = userService.getTotalUserCount();
        long activeEventCount = eventService.getActiveEventCount();
        
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeEventCount", activeEventCount);
        
        return "admin/dashboard";
    }
}