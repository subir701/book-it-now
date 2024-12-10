package com.bookItNow.service;

import com.bookItNow.entity.Payment;

import java.util.Optional;

public interface PaymentService {

    Payment processPayment(Payment payment);
    Optional<Payment> findByBookingId(Integer bookingId);
}
