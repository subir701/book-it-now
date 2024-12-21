package com.bookItNow.service;

import com.bookItNow.entity.Event;
import com.bookItNow.entity.Seat;
import com.bookItNow.entity.Section;
import com.bookItNow.exception.SectionNotFoundException;
import com.bookItNow.repository.EventRepository;
import com.bookItNow.repository.SeatRepository;
import com.bookItNow.repository.SectionRepository;
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
        return sectionRepository.findById(id).orElseThrow(()-> new SectionNotFoundException("Section not found!!"));
    }

    @Override
    public Section createSection(int eventId, Section section) throws RuntimeException {
        // Fetch the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("No event found!!"));

        // Validate section capacity and price
        if (section.getCapacity() <= 0 || section.getPrice() <= 0) {
            throw new RuntimeException("Capacity and price must be greater than 0");
        }

        // Associate section with the event
        section.setEvent(event);

        // Initialize seats list if null
        if (section.getSeats() == null) {
            section.setSeats(new ArrayList<>());
        }

        // Save the section first
        Section savedSection = sectionRepository.save(section);

        // Generate dynamic section label
        int size = event.getSections().size();
        String sectionLabel = getSectionLabel(size);

        // Create seats and associate with the saved section
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= section.getCapacity(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(sectionLabel + i);
            seat.setSection(savedSection);
            seat.setPrice(section.getPrice());
            seats.add(seat);
        }

        // Save seats
        seatRepository.saveAll(seats);
        savedSection.getSeats().addAll(seats);


        // Add the saved section to the event
        event.getSections().add(savedSection);
        eventRepository.save(event);

        return sectionRepository.save(savedSection);
    }

    // Utility method for dynamic section label
    private String getSectionLabel(int size) {
        StringBuilder label = new StringBuilder();
        while (size >= 0) {
            label.insert(0, (char) ('A' + (size % 26)));
            size = (size / 26) - 1;
        }
        return label.toString();
    }

    @Override
    public Section updateSectionName(int id, String name) throws SectionNotFoundException {
        Section section = findSectionById(id);
        section.setName(name);
        return sectionRepository.save(section);
    }

    @Override
    public Section updateSectionCapacity(int id, int updateCapacity) throws SectionNotFoundException {
        Section section = findSectionById(id);
        List<Seat> seats = section.getSeats();

        if (updateCapacity > seats.size()) {
            // Add new seats
            for (int i = seats.size() + 1; i <= updateCapacity; i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(section.getName() + i); // Use section name + number
                seat.setSection(section);
                seats.add(seat);
            }
        } else if (updateCapacity < seats.size()) {
            // Reduce seats, but ensure no booked seat is removed
            for (int i = seats.size() - 1; i >= updateCapacity; i--) {
                if (seats.get(i).isBooked()) {
                    throw new RuntimeException("Cannot reduce capacity: Some seats are already booked.");
                }
                seats.remove(i);
            }
        }

        section.setCapacity(updateCapacity);
        return sectionRepository.save(section);
    }

    public String deleteSection(Integer Id) throws SectionNotFoundException{
        Section section = sectionRepository.findById(Id).orElseThrow(()-> new SectionNotFoundException("No section found !!"));
        sectionRepository.delete(section);
        return "Section Deleted";
    }
}
