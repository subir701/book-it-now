package com.bookItNow.service;

import com.bookItNow.entity.Availability;
import com.bookItNow.entity.Event;
import com.bookItNow.entity.Section;
import com.bookItNow.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> findByVenue(String venue) {
        return eventRepository.findByVenue(venue);
    }

    @Override
    public List<Event> findUpcomingEvents(LocalDateTime dateTime) {
        return eventRepository.findByDateTimeAfter(dateTime);
    }

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> findAllAvailableEvents() {
        return eventRepository.findByIsAvailable(Availability.AVAILABLE).stream().sorted(Comparator.comparing(Event::getDateTime)).toList();
    }

}
