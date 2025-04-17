package com.bookItNow.event.service;

import com.bookItNow.event.model.Event;
import com.bookItNow.event.model.Seat;
import com.bookItNow.event.model.Section;
import com.bookItNow.event.exception.SectionNotFoundException;
import com.bookItNow.event.repository.EventRepository;
import com.bookItNow.event.repository.SeatRepository;
import com.bookItNow.event.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    EventRepository eventRepository;

    @Override
    public Section findSectionById(int id) throws SectionNotFoundException {
        // Fetch section by its ID or throw an exception if not found.
        return sectionRepository.findById(id)
                .orElseThrow(() -> new SectionNotFoundException("Section not found!!"));
    }

    @Override
    public Section createSection(int eventId, Section section) throws RuntimeException {
        // Fetch the event associated with the section.
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("No event found!!"));

        // Validate that the section's capacity and price are valid.
        if (section.getCapacity() <= 0 || section.getPrice() <= 0) {
            throw new RuntimeException("Capacity and price must be greater than 0");
        }

        // Associate the section with the event.
        section.setEvent(event);

        // Initialize the seats list for the section if it's null.
        if (section.getSeats() == null) {
            section.setSeats(new ArrayList<>());
        }


        // Generate a dynamic section label (e.g., A, B, AA, etc.).
        int size = sectionRepository.countByEventId(eventId);
        String sectionLabel = getSectionLabel(size);

        // Create and associate seats with the saved section.
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= section.getCapacity(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(sectionLabel + i); // e.g., A1, B2
            seat.setSection(section);
            seat.setPrice(section.getPrice());
            seats.add(seat);
        }

        // Associate seats with the section before saving.
        section.setSeats(seats);

        // Save the section and cascade to seats (if cascade is set properly in your entity).
        return sectionRepository.save(section);
    }

    // Utility method to generate dynamic section labels like A, B, AA, AB, etc.
    private String getSectionLabel(int size) {
        StringBuilder label = new StringBuilder();
        while (size >= 0) {
            label.insert(0, (char) ('A' + (size % 26))); // Convert size to corresponding letter.
            size = (size / 26) - 1; // Reduce size for the next character.
        }
        return label.toString();
    }

    @Override
    public Section updateSectionName(int id, String name) throws SectionNotFoundException {
        // Find the section by ID and update its name.
        Section section = findSectionById(id);
        section.setName(name);
        return sectionRepository.save(section);
    }

    @Override
    public Section updateSectionCapacity(int id, int updateCapacity) throws SectionNotFoundException {
        // Fetch the section by ID and its current seats.
        Section section = findSectionById(id);
        List<Seat> seats = section.getSeats();

        if (updateCapacity > seats.size()) {
            // Add new seats if the updated capacity exceeds current seat count.
            for (int i = seats.size() + 1; i <= updateCapacity; i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(section.getName() + i); // Use section name + number for new seats.
                seat.setSection(section);
                seats.add(seat);
            }
        } else if (updateCapacity < seats.size()) {
            // Reduce seats, but ensure no booked seat is being removed.
            for (int i = seats.size() - 1; i >= updateCapacity; i--) {
                if (seats.get(i).isBooked()) {
                    throw new RuntimeException("Cannot reduce capacity: Some seats are already booked.");
                }
                seats.remove(i); // Remove seats from the list.
            }
        }

        // Update the section's capacity and save the changes.
        section.setCapacity(updateCapacity);
        return sectionRepository.save(section);
    }

    @Override
    public String deleteSection(Integer id) throws SectionNotFoundException {
        // Fetch the section by ID and delete it. Throw an exception if not found.
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new SectionNotFoundException("No section found!!"));
        sectionRepository.delete(section);
        return "Section Deleted";
    }
}
