package com.bookItNow.service;

import com.bookItNow.entity.Seat;
import com.bookItNow.exception.SeatNotFoundException;

import java.util.List;
import java.util.Optional;

public interface SeatService {

    public Seat getSeat(int seatNo)throws SeatNotFoundException;
    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException;
    public List<Seat> getAllSeats()throws SeatNotFoundException;
    public void selectSeats(int userId, int seatIds) throws RuntimeException;
}
