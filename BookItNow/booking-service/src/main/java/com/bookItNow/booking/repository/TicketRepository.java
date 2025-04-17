package com.bookItNow.booking.repository;


import com.bookItNow.booking.model.Ticket;
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

    // Find all tickets associated with a specific booking ID
    List<Ticket> findByBookingId(Integer bookingId);
    // This is super useful when you want to fetch all tickets for a particular booking.
    // It leverages Spring Data JPA’s ability to derive queries from method names. No manual SQL needed!

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Integer id);
    // Adding a pessimistic lock ensures that no one else can modify the ticket while you're working with it.
    // This can come in handy in concurrent scenarios like ticket booking systems.
    // The `@Query` annotation lets you write JPQL (Java Persistence Query Language) for custom queries.
}
