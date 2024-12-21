package com.bookItNow.controller;

import com.bookItNow.entity.Booking;
import com.bookItNow.exception.UserNotFoundException;
import com.bookItNow.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createBooking(@PathVariable Integer userId) throws UserNotFoundException {
        return new ResponseEntity<>(bookingService.createBooking(userId), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(bookingService.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}

