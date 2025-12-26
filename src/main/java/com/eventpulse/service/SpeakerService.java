package com.eventpulse.service;

import com.eventpulse.model.Speaker;
import com.eventpulse.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Transactional
    public Speaker createSpeaker(Speaker speaker) {
        return speakerRepository.save(speaker);
    }

    @Transactional
    public List<Speaker> saveSpeakers(List<Speaker> speakers) {
        return speakerRepository.saveAll(speakers);
    }

    @Transactional
    public Speaker updateSpeaker(Speaker speaker) {
        if (!speakerRepository.existsById(speaker.getId())) {
            throw new RuntimeException("Speaker not found with id: " + speaker.getId());
        }
        return speakerRepository.save(speaker);
    }

    @Transactional
    public void deleteSpeaker(Long id) {
        speakerRepository.deleteById(id);
    }

    public Speaker getSpeakerById(Long id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker not found with id: " + id));
    }
}