package com.bookItNow.event.service;

import com.bookItNow.event.dto.EventSummaryDTO;
import com.bookItNow.event.model.Event;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event createEvent(Event event);
    Event updateEvent(Event event);
    void deleteEvent(Integer eventId);
    List<Event> findByVenue(String venue);
    List<Event> findUpcomingEvents(LocalDateTime dateTime);
    List<Event> findAllEvents();
    Page<EventSummaryDTO> findAllAvailableEvents(int page, int size);
    Event getEventDetails(int id);
}
