package com.bookItNow.user.exception;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException() {}
    public SectionNotFoundException(String message) {
        super(message);
    }
}
