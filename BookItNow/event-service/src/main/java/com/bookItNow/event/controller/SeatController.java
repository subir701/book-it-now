package com.bookItNow.event.controller;

import com.bookItNow.event.model.Seat;
import com.bookItNow.event.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookitnow/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    /**
     * Create a new seat in a specific section.
     *
     * @param seat      The seat details.
     * @param sectionId The ID of the section to which the seat belongs.
     * @return The created seat.
     * @throws RuntimeException If the section is not found or seat creation fails.
     */
    @PostMapping("/section/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Seat> addSeatToSection(
            @Valid @RequestBody Seat seat,
            @PathVariable Integer sectionId
    ) {
        Seat createdSeat = seatService.createSeat(seat, sectionId);
        return new ResponseEntity<>(createdSeat, HttpStatus.CREATED);
    }

    @PostMapping("/selected")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> selectingSeat(@AuthenticationPrincipal String userId, @RequestParam Integer eventId, @RequestBody List<Integer> selectedSeatNo){

        int id = Integer.parseInt(userId);
        seatService.selectSeats(id,eventId,selectedSeatNo);
        return ResponseEntity.ok("Seat selected sucessfully");
    }

    @GetMapping("/{eventId}/available")
    public ResponseEntity<List<Seat>> getAllAvailableSeats(@PathVariable Integer eventId){
        return ResponseEntity.ok(seatService.getAllAvailableSeats(eventId));
    }
}
