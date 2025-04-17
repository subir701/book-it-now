package com.bookItNow.event.service;

import com.bookItNow.common.event.CancelReservationEvent;
import com.bookItNow.common.event.SeatStatusUpdateEvent;
import com.bookItNow.event.exception.AlreadyReservedException;
import com.bookItNow.event.model.Seat;
import com.bookItNow.event.exception.EventNotFoundException;
import com.bookItNow.event.exception.SeatNotFoundException;

import java.util.List;

public interface SeatService {

    public Seat getSeat(int seatNo)throws SeatNotFoundException;

    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException;

    public List<Seat> getAllSeats()throws SeatNotFoundException;

    public void selectSeats(int userId, int eventId, List<Integer> seatIds) throws RuntimeException;

    public List<Seat> getAllAvailableSeats(int eventId) throws SeatNotFoundException, EventNotFoundException;

    public void cancelSelection(CancelReservationEvent event);

    public void updateSeatStatus(SeatStatusUpdateEvent event)throws SeatNotFoundException, AlreadyReservedException;

}
