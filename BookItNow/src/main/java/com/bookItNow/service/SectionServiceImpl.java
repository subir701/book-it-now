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
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("No event found!!"));

        if(section.getCapacity()>0 && section.getPrice()>0){
            int size = section.getEvent().getSections().size();
            List<Seat> list = section.getSeats();
            if (list == null) {
                list = new ArrayList<>(); // Initialize if null
            }
            String sectionLabel = getSectionLabel(size); // Generate dynamic section label

            for(int i=1; i<=section.getCapacity(); i++){
                Seat seat = new Seat();
                seat.setSeatNumber(sectionLabel+i);
                seat.setSection(section);
                seat.setPrice(section.getPrice());
                list.add(seat);
            }
            section.setSeats(list);
            seatRepository.saveAll(list);
        }else{
            throw new RuntimeException("Capacity should be greater than 0");
        }

        // Associate section with the event
        section.setEvent(event);
        event.getSections().add(section);

        eventRepository.save(event);
        return sectionRepository.save(section);
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
}
