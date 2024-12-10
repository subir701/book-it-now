package com.bookItNow.service;

import com.bookItNow.entity.Payment;
import com.bookItNow.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByBookingId(Integer bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
