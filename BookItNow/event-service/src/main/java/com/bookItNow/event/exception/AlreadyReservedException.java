package com.bookItNow.event.exception;

public class AlreadyReservedException extends RuntimeException {

    AlreadyReservedException(String message) {
        super(message);
    }
}
