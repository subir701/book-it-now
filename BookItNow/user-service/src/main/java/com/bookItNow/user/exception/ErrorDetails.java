package com.bookItNow.exception;

import java.time.LocalDateTime;

public class ErrorDetails {
    private String message;
    private LocalDateTime timestamp;
    private String description;

    public ErrorDetails(String message, LocalDateTime timestamp, String description) {
        this.message = message;
        this.timestamp = timestamp;
        this.description = description;
    }

    public ErrorDetails() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
