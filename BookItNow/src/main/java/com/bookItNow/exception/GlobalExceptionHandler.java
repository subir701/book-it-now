package com.bookItNow.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> userNotFoundException(UserNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorDetails> eventNotFoundException(EventNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorDetails> bookingNotFoundException(BookingNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }
}
