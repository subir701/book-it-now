package com.bookItNow.repository;

import com.bookItNow.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Custom query to find bookings by user ID
    List<Booking> findByUserId(Integer userId);
}
