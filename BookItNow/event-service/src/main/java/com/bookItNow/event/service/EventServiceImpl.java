package com.bookItNow.event.service;

import com.bookItNow.common.Enum.Availability;
import com.bookItNow.event.dto.EventSummaryDTO;
import com.bookItNow.event.model.Event;
import com.bookItNow.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository; // Injecting the repository to handle database operations for Event.

    @Override
    public Event createEvent(Event event) {
        // Save the provided event to the database and return the saved instance.
        log.info("Create Event: {}", event.getName());

        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        // Update an existing event by saving the provided event entity to the database.
        log.info("Update Event: {}", event.getId());

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer eventId) {
        // Delete an event from the database by its ID.
        log.info("Delete Event: {}", eventId);

        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> findByVenue(String venue) {
        // Retrieve all events that match the given venue name.
        log.info("Fetching list of all events on the basis of venue {}", venue);

        return eventRepository.findByVenue(venue);
    }

    @Override
    public List<Event> findUpcomingEvents(LocalDateTime dateTime) {
        // Fetch events that are scheduled after the specified date and time.
        log.info("Fetching List of events that are scheduled after the specified data and time: {}", dateTime);

        return eventRepository.findByDateTimeAfter(dateTime);
    }

    @Override
    public List<Event> findAllEvents() {
        // Fetch all events from the database.
        log.info("Fetching list of all events");

        return eventRepository.findAll();
    }

    /**
     * UPDATED: Uses the deep fetch query to avoid N+1 issues when
     * showing the seat map.
     */
    public Event getEventDetails(int id) {
        log.info("Fetching deep details for event ID: {}", id);
        return eventRepository.findEventWithDeepDetails(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    /**
     * UPDATED: Now supports Pagination and DTO mapping.
     * This is the method the Controller should call for the main list.
     */
    public Page<EventSummaryDTO> findAllAvailableEvents(int page, int size) {
        log.info("Fetching paginated available event summaries");

        // org.springframework.data.domain.PageRequest
        Pageable pageable = PageRequest.of(page, size);

        return eventRepository.findSummariesByAvailability(Availability.AVAILABLE, pageable);
    }

}
