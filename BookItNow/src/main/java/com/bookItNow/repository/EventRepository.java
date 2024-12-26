package com.bookItNow.repository;

import com.bookItNow.model.Availability;
import com.bookItNow.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {


    List<Event> findByVenue(String venue);


    List<Event> findByDateTimeAfter(java.time.LocalDateTime dateTime);

    List<Event> findByIsAvailable(Availability availability);
}
