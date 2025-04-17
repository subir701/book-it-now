package com.bookItNow.booking.repository;


import com.bookItNow.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}
