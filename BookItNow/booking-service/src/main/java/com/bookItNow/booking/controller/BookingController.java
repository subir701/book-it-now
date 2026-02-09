package com.bookItNow.booking.controller;

import com.bookItNow.booking.model.Booking;

import com.bookItNow.booking.service.BookingService;
import com.bookItNow.common.event.ConfirmedPaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookitnow/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create a new booking for a user.
     *
     * @param event The ID of the user for whom the booking is being created.
     * @return The created booking.
     */
    @PostMapping("/new/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Booking> addBooking(@RequestBody ConfirmedPaymentEvent event)  {
        Booking booking = bookingService.createBooking(event);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    /**
     * Retrieve all bookings for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of bookings associated with the user.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Booking>> fetchBookingsByUser(@AuthenticationPrincipal String userId) {
        int id = Integer.parseInt(userId);
        List<Booking> bookings = bookingService.findByUserId(id);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Delete a booking by ID.
     *
     * @param id The ID of the booking to be deleted.
     * @return No content status upon successful deletion.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> removeBooking(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
