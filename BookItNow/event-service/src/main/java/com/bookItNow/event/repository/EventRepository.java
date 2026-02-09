package com.bookItNow.event.repository;


import com.bookItNow.common.Enum.Availability;
import com.bookItNow.event.dto.EventSummaryDTO;
import com.bookItNow.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {


    List<Event> findByVenue(String venue);


    List<Event> findByDateTimeAfter(java.time.LocalDateTime dateTime);

    Page<Event> findByIsAvailable(Availability availability, Pageable pageable);

    // Use this for the actual "Browse Events" screen
    @Query("SELECT new com.bookItNow.event.dto.EventSummaryDTO(" +
            "e.id, e.name, e.venue, " +
            "(SELECT COUNT(s) FROM Seat s WHERE s.section.event = e AND s.isBooked = false), " +
            "(SELECT MIN(sec.price) FROM Section sec WHERE sec.event = e)) " +
            "FROM Event e " +
            "WHERE e.isAvailable = :availability")
    Page<EventSummaryDTO> findSummariesByAvailability(Availability availability, Pageable pageable);

    /**
     * This query fetches the Event, all its Sections, and all their Seats
     * in one single database round-trip.
     */
    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.sections s " +
            "LEFT JOIN FETCH s.seats " +
            "WHERE e.id = :id")
    Optional<Event> findEventWithDeepDetails(@Param("id") int id);
}
