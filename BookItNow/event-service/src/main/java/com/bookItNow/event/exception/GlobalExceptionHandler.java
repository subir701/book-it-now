package com.bookItNow.event.exception;


import com.bookItNow.event.exception.EventNotFoundException;
import com.bookItNow.event.exception.SeatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorDetails> eventNotFoundException(EventNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorDetails> seatNotFoundException(SeatNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<ErrorDetails> sectionNotFoundException(SectionNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(), LocalDateTime.now(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyReservedException.class)
    public ResponseEntity<ErrorDetails> alreadyReservedException(AlreadyReservedException ex, WebRequest request) {
        return new ResponseEntity<ErrorDetails>(new ErrorDetails(ex.getMessage(),LocalDateTime.now(), request.getDescription(false)), HttpStatus.CONFLICT);
    }
}
