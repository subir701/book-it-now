package com.bookItNow.service;

import com.bookItNow.entity.Event;
import com.bookItNow.entity.Seat;
import com.bookItNow.entity.Section;
import com.bookItNow.entity.User;
import com.bookItNow.exception.EventNotFoundException;
import com.bookItNow.exception.SeatNotFoundException;
import com.bookItNow.exception.SectionNotFoundException;
import com.bookItNow.repository.EventRepository;
import com.bookItNow.repository.SeatRepository;
import com.bookItNow.repository.SectionRepository;
import com.bookItNow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventRepository eventRepository;

    @Override
    public Seat getSeat(int seatNo) throws SeatNotFoundException {
        // Fetch a seat by its ID. If it doesn't exist, throw an exception.
        return seatRepository.findById(seatNo)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found for ID: " + seatNo));
    }

    @Override
    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException {
        // Fetch the section where the seat will be added.
        Section section = sectionRepository.findById(sectionNo)
                .orElseThrow(() -> new SectionNotFoundException("Section not found!"));

        // Check if the section's capacity allows adding a new seat.
        if (section.getSeats().size() >= section.getCapacity()) {
            throw new RuntimeException("Cannot add seat: Section capacity exceeded!");
        }

        // Associate the seat with the section and save it.
        seat.setSection(section);
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getAllSeats() throws SeatNotFoundException {
        // Fetch all seats from the database. If none exist, throw an exception.
        List<Seat> list = seatRepository.findAll();
        if (list.isEmpty()) {
            throw new SeatNotFoundException("Seat not found!!!");
        }
        return list;
    }

    @Override
    public void selectSeats(int userId, int seatIds) throws RuntimeException {
        // Fetch the user by ID. If not found, throw an exception.
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No User Found!!"));

        // Fetch the seat and check if it's already booked.
        Seat seat = getSeat(seatIds);
        if (seat.isBooked()) {
            throw new RuntimeException("Seat is already booked: " + seat.getSeatNumber());
        }

        // Initialize the user's selected seat list if it's null.
        if (user.getSelectedSeatIds() == null) {
            user.setSelectedSeatIds(new ArrayList<>());
        }

        // Add the seat to the user's selected seat list if it's not already there.
        if (!user.getSelectedSeatIds().contains(seat.getId())) {
            user.getSelectedSeatIds().add(seat.getId());
            userRepository.save(user); // Save the user after updating their seat list.
        }
    }

    @Override
    public List<Seat> getAllAvailableSeats(int eventId) throws SeatNotFoundException, EventNotFoundException {
        // Fetch the event by ID. If not found, throw an exception.
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found!"));

        // Extract all seats from the sections of the event and filter out booked ones.
        List<Seat> availableSeats = event.getSections().stream()
                .flatMap(section -> section.getSeats().stream()) // Combine all seats from all sections.
                .filter(seat -> !seat.isBooked()) // Keep only unbooked seats.
                .collect(Collectors.toList());

        // If no available seats are found, throw an exception.
        if (availableSeats.isEmpty()) {
            throw new SeatNotFoundException("No available seats found for this event.");
        }

        return availableSeats;
    }
}
