package com.bookItNow.exception;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException() {}
    public SectionNotFoundException(String message) {
        super(message);
    }
}
