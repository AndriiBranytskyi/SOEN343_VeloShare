package com.veloshare.domain;

import java.util.Date;

public class Event {

    private String eventId;
    private String type;
    private Date timestamp;
    private String details;

    public Event(String eventId, String type, String details) {
        this.eventId = eventId;
        this.type = type;
        this.timestamp = new Date();
        this.details = details;
    }

    public String getEventId() {
        return eventId;
    }

    public String getType() {
        return type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void log() {
        System.out.println("Event ID: " + eventId + ", Type: " + type + ", Timestamp: " + timestamp + ", Details: " + details);
    }
}
