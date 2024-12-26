package com.bookItNow.service;

import com.bookItNow.model.Availability;
import com.bookItNow.model.Event;
import com.bookItNow.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository; // Injecting the repository to handle database operations for Event.

    @Override
    public Event createEvent(Event event) {
        // Save the provided event to the database and return the saved instance.
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        // Update an existing event by saving the provided event entity to the database.
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer eventId) {
        // Delete an event from the database by its ID.
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> findByVenue(String venue) {
        // Retrieve all events that match the given venue name.
        return eventRepository.findByVenue(venue);
    }

    @Override
    public List<Event> findUpcomingEvents(LocalDateTime dateTime) {
        // Fetch events that are scheduled after the specified date and time.
        return eventRepository.findByDateTimeAfter(dateTime);
    }

    @Override
    public List<Event> findAllEvents() {
        // Fetch all events from the database.
        return eventRepository.findAll();
    }

    @Override
    public List<Event> findAllAvailableEvents() {
        // Fetch all events that are marked as available, then sort them by their date and time.
        return eventRepository.findByIsAvailable(Availability.AVAILABLE)
                .stream()
                .sorted(Comparator.comparing(Event::getDateTime))
                .toList();
    }
}
