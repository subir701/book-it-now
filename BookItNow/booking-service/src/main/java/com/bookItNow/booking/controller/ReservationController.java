package com.bookItNow.booking.controller;

import com.bookItNow.booking.exception.ReservationNotFoundException;
import com.bookItNow.booking.model.Reservation;
import com.bookItNow.booking.service.ReservationService;
import com.bookItNow.common.dto.ReservationDTO;
import com.bookItNow.common.event.ReservationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bookitnow/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/get/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable int id) throws ReservationNotFoundException {
        Reservation reservation = reservationService.findReservation(id);
        ReservationDTO reservationDTO = new ReservationDTO(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getEventId(),
                reservation.getSeatDetails()
        );

        return ResponseEntity.ok(reservationDTO);
    }

}
