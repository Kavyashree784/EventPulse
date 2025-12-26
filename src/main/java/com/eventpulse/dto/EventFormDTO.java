package com.eventpulse.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventFormDTO {
    private Long id;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String location;
    private Integer capacity;
    private boolean accessible;
    private List<String> speakerNames;
    private List<String> speakerDescriptions;
}