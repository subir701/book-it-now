package com.bookItNow.service;

import com.bookItNow.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Integer userId);
    List<Booking> findByUserId(Integer userId);
    void deleteBooking(Integer bookingId);
}
