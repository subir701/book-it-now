package com.bookItNow.controller;

import com.bookItNow.entity.Seat;
import com.bookItNow.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * Create a new seat in a specific section.
     *
     * @param seat      The seat details.
     * @param sectionId The ID of the section to which the seat belongs.
     * @return The created seat.
     * @throws RuntimeException If the section is not found or seat creation fails.
     */
    @PostMapping("/section/{sectionId}")
    public ResponseEntity<Seat> addSeatToSection(
            @Valid @RequestBody Seat seat,
            @PathVariable Integer sectionId
    ) {
        Seat createdSeat = seatService.createSeat(seat, sectionId);
        return new ResponseEntity<>(createdSeat, HttpStatus.CREATED);
    }
}
