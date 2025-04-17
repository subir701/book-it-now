package com.bookItNow.booking.service;

import com.bookItNow.booking.model.Booking;
import com.bookItNow.booking.model.Reservation;
import com.bookItNow.common.event.ConfirmedPaymentEvent;

import java.util.List;

public interface BookingService {

    Booking createBooking(ConfirmedPaymentEvent event);
    List<Booking> findByUserId(Integer userId);
    void deleteBooking(Integer bookingId);
}
