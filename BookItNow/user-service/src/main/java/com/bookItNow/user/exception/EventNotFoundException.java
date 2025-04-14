package com.bookItNow.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException() {
    }

    public EventNotFoundException(String message) {
        super(message);
    }
}
