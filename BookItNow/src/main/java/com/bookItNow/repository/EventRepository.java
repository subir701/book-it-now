package com.bookItNow.repository;

import com.bookItNow.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {


    List<Event> findByVenue(String venue);


    List<Event> findByDateTimeAfter(java.time.LocalDateTime dateTime);
}
