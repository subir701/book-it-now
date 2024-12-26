package com.bookItNow.controller;

import com.bookItNow.model.Event;
import com.bookItNow.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Create a new event.
     *
     * @param event The event details to be created.
     * @return The created event.
     */
    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    /**
     * Update an existing event by ID.
     *
     * @param id    The ID of the event to be updated.
     * @param event The updated event details.
     * @return The updated event.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> modifyEvent(@PathVariable Integer id, @RequestBody Event event) {
        event.setId(id);
        Event updatedEvent = eventService.updateEvent(event);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete an event by ID.
     *
     * @param id The ID of the event to be deleted.
     * @return No content status after successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve all events by venue.
     *
     * @param venue The venue name.
     * @return A list of events held at the specified venue.
     */
    @GetMapping("/venue/{venue}")
    public ResponseEntity<List<Event>> fetchEventsByVenue(@PathVariable String venue) {
        List<Event> events = eventService.findByVenue(venue);
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieve all upcoming events.
     *
     * @return A list of upcoming events starting from the current date and time.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> fetchUpcomingEvents() {
        List<Event> events = eventService.findUpcomingEvents(LocalDateTime.now());
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieve all available events.
     *
     * @return A list of events with available seats.
     */
    @GetMapping("/available")
    public ResponseEntity<List<Event>> fetchAllAvailableEvents() {
        List<Event> events = eventService.findAllAvailableEvents();
        return ResponseEntity.ok(events);
    }
}
