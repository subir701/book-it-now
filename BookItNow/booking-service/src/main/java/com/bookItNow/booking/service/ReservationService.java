package com.bookItNow.booking.service;

import com.bookItNow.booking.exception.ReservationNotFoundException;
import com.bookItNow.booking.model.Reservation;
import com.bookItNow.common.event.ReservationEvent;

public interface ReservationService {
    void createReservation(ReservationEvent reservationEvent);
    Reservation findReservation(Integer reservationId) throws ReservationNotFoundException;
    void cancelReservation(Integer reservationId) throws ReservationNotFoundException;
}
