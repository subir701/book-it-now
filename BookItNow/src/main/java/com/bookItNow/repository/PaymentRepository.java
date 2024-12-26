package com.bookItNow.repository;

import com.bookItNow.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // Custom query to find payments by booking ID
    Optional<Payment> findByBookingId(Integer bookingId);
}
