package com.bookItNow.event.repository;


import com.bookItNow.common.Enum.Availability;
import com.bookItNow.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {


    List<Event> findByVenue(String venue);


    List<Event> findByDateTimeAfter(java.time.LocalDateTime dateTime);

    List<Event> findByIsAvailable(Availability availability);
}
