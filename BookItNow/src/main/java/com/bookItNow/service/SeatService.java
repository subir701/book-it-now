package com.bookItNow.service;

import com.bookItNow.model.Seat;
import com.bookItNow.exception.EventNotFoundException;
import com.bookItNow.exception.SeatNotFoundException;

import java.util.List;

public interface SeatService {

    public Seat getSeat(int seatNo)throws SeatNotFoundException;
    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException;
    public List<Seat> getAllSeats()throws SeatNotFoundException;
    public void selectSeats(int userId, int seatIds) throws RuntimeException;
    public List<Seat> getAllAvailableSeats(int eventId) throws SeatNotFoundException, EventNotFoundException;
}
