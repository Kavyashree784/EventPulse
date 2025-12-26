package com.eventpulse.config;

import com.eventpulse.model.Event;
import com.eventpulse.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(String... args) {
        // Only add sample data if no events exist
        if (eventRepository.count() == 0) {
            // Sample Event 1: Tech Conference
            Event event1 = new Event();
            event1.setTitle("Sustainable Tech Conference 2025");
            event1.setDescription("Join us for a day of exploring sustainable technology solutions. Topics include renewable energy in computing, green software practices, and eco-friendly hardware design.");
            event1.setStartTime(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
            event1.setEndTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
            event1.setLocation("Innovation Hub, Chennai");
            event1.setCapacity(200);
            event1.setAccessible(true);
            eventRepository.save(event1);

            // Sample Event 2: AI Workshop
            Event event2 = new Event();
            event2.setTitle("AI Ethics Workshop");
            event2.setDescription("Interactive workshop on ethical considerations in AI development. Discussion on bias, fairness, and responsible AI implementation.");
            event2.setStartTime(LocalDateTime.now().plusDays(14).withHour(14).withMinute(0));
            event2.setEndTime(LocalDateTime.now().plusDays(14).withHour(17).withMinute(0));
            event2.setLocation("Digital Learning Center, Bangalore");
            event2.setCapacity(50);
            event2.setAccessible(true);
            eventRepository.save(event2);

            // Sample Event 3: Hackathon
            Event event3 = new Event();
            event3.setTitle("Climate Action Hackathon");
            event3.setDescription("48-hour hackathon focused on developing innovative solutions for climate change. Open to developers, designers, and environmental scientists.");
            event3.setStartTime(LocalDateTime.now().plusDays(21).withHour(10).withMinute(0));
            event3.setEndTime(LocalDateTime.now().plusDays(23).withHour(10).withMinute(0));
            event3.setLocation("Green Tech Park, Mumbai");
            event3.setCapacity(100);
            event3.setAccessible(true);
            eventRepository.save(event3);

            System.out.println("Sample events have been created.");
        }
    }
}