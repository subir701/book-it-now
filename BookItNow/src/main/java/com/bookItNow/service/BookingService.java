package com.bookItNow.service;

import com.bookItNow.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking);
    List<Booking> findByUserId(Integer userId);
    void deleteBooking(Integer bookingId);
}