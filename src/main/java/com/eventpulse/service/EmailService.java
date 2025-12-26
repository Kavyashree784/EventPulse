package com.eventpulse.service;

import com.eventpulse.model.SpeakerRsvp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    @Transactional
    public void sendRsvpEmail(SpeakerRsvp rsvp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(rsvp.getSpeakerEmail());
            message.setSubject("Speaker Invitation: " + rsvp.getEvent().getTitle());
            message.setFrom("ghoraav.kc2022@vitstudent.ac.in");
            
            String emailBody = String.format("""
                Dear %s,

                You are cordially invited as a speaker for the event: %s

                Event Details:
                Date: %s
                Time: %s to %s
                Location: %s

                Please confirm your availability for this speaking engagement.

                Best regards,
                EventPulse Team
                """,
                rsvp.getSpeakerName(),
                rsvp.getEvent().getTitle(),
                rsvp.getStartDate().format(DATE_FORMATTER),
                rsvp.getStartDate().format(TIME_FORMATTER),
                rsvp.getEndDate().format(TIME_FORMATTER),
                rsvp.getEvent().getLocation()
            );

            message.setText(emailBody);
            
            System.out.println("Attempting to send email to: " + rsvp.getSpeakerEmail());
            System.out.println("Using SMTP host: " + mailSender.toString());
            
            mailSender.send(message);
            System.out.println("Email sent successfully!");
            
        } catch (Exception e) {
            System.err.println("Failed to send email. Error details:");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}