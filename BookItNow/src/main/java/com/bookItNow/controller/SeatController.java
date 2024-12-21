package com.bookItNow.controller;

import com.bookItNow.entity.Seat;
import com.bookItNow.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping("/{sectionId}")
    public ResponseEntity<?> createSeat(@Valid @RequestBody Seat seat, @PathVariable Integer sectionId) throws RuntimeException {
        return new ResponseEntity<>(seatService.createSeat(seat, sectionId), HttpStatus.CREATED);
    }

}
