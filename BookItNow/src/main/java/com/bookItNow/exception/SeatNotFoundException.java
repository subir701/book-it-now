package com.bookItNow.exception;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException() {
    }
    public SeatNotFoundException(String message) {
        super(message);
    }
}
