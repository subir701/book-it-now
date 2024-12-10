package com.bookItNow.repository;


import com.bookItNow.entity.Event;
import com.bookItNow.entity.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Custom query to find tickets by event ID
    List<Ticket> findByEventId(Integer eventId);

    // Custom query to find tickets by booking ID
    List<Ticket> findByBookingId(Integer bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Integer id);
}
