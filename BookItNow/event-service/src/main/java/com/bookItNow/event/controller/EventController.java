package com.bookItNow.event.controller;

import com.bookItNow.event.dto.EventSummaryDTO;
import com.bookItNow.event.model.Event;
import com.bookItNow.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bookitnow/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // --- ADMIN ENDPOINTS ---

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> modifyEvent(@PathVariable Integer id, @RequestBody Event event) {
        event.setId(id);
        return ResponseEntity.ok(eventService.updateEvent(event));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // --- USER / PUBLIC ENDPOINTS ---

    /**
     * OPTIMIZED: Fetch full event details including sections and seats.
     * Used when a user clicks on a specific event.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> fetchEventDetails(@PathVariable Integer id) {
        // Uses findEventWithDeepDetails (JOIN FETCH)
        return ResponseEntity.ok(eventService.getEventDetails(id));
    }

    /**
     * OPTIMIZED: Paginated list of available event summaries.
     * Returning Page<EventSummaryDTO> instead of List<Event>.
     */
    @GetMapping("/available")
    public ResponseEntity<Page<EventSummaryDTO>> fetchAllAvailableEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Uses the DTO projection and pagination logic
        return ResponseEntity.ok(eventService.findAllAvailableEvents(page, size));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> fetchUpcomingEvents() {
        return ResponseEntity.ok(eventService.findUpcomingEvents(LocalDateTime.now()));
    }

    @GetMapping("/venue/{venue}")
    public ResponseEntity<List<Event>> fetchEventsByVenue(@PathVariable String venue) {
        return ResponseEntity.ok(eventService.findByVenue(venue));
    }
}