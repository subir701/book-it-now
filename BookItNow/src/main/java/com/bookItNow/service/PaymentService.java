package com.bookItNow.service;

import com.bookItNow.model.Payment;

import java.util.Optional;

public interface PaymentService {

    Payment processPayment(Payment payment);
    Optional<Payment> findByBookingId(Integer bookingId);
}
