package com.bookItNow.booking.exception;


import com.bookItNow.booking.exception.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorDetails> reservationNotFound(ReservationNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage(), request.getDescription(false)),HttpStatus.NOT_FOUND);
    }
}
