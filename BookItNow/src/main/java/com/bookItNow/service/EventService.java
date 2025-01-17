package com.bookItNow.service;

import com.bookItNow.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event createEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(Integer eventId);
    List<Event> findByVenue(String venue);
    List<Event> findUpcomingEvents(LocalDateTime dateTime);
    List<Event> findAllEvents();
    List<Event> findAllAvailableEvents();
}
