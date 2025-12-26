package com.eventpulse.controller;

import com.eventpulse.model.Event;
import com.eventpulse.model.EventStatus;
import com.eventpulse.model.User;
import com.eventpulse.service.EventService;
import com.eventpulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Event> upcomingEvents = eventService.getEventsByStatus(EventStatus.UPCOMING);
        List<Event> ongoingEvents = eventService.getEventsByStatus(EventStatus.ONGOING);
        List<Event> completedEvents = eventService.getEventsByStatus(EventStatus.COMPLETED);

        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("ongoingEvents", ongoingEvents);
        model.addAttribute("completedEvents", completedEvents);
        model.addAttribute("currentUser", userService.findByEmail(userDetails.getUsername()));

        return "dashboard";
    }

    @PostMapping("/events/{id}/register")
    public String registerForEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        eventService.registerUserForEvent(id, userDetails.getUsername());
        return "redirect:/dashboard";
    }

    @PostMapping("/events/{id}/unregister")
    public String unregisterFromEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        eventService.unregisterUserFromEvent(id, userDetails.getUsername());
        return "redirect:/dashboard";
    }
}