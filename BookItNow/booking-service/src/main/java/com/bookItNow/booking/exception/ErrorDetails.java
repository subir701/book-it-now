package com.bookItNow.booking.exception;

import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

public class ErrorDetails {
    private String message;
    private LocalDateTime timestamp;
    private String description;

    public ErrorDetails(String message, String description) {
        this.message = message;
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
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
