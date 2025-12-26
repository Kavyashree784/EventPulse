package com.eventpulse.controller;

import com.eventpulse.model.Event;
import com.eventpulse.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> all() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event one(@PathVariable Long id) {
        return eventService.getEventById(id);
    }
}
