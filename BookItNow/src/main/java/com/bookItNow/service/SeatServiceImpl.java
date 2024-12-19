package com.bookItNow.service;

import com.bookItNow.entity.Seat;
import com.bookItNow.entity.Section;
import com.bookItNow.entity.User;
import com.bookItNow.exception.SeatNotFoundException;
import com.bookItNow.exception.SectionNotFoundException;
import com.bookItNow.repository.SeatRepository;
import com.bookItNow.repository.SectionRepository;
import com.bookItNow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Seat getSeat(int seatNo) throws SeatNotFoundException {
        return seatRepository.findById(seatNo)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found for ID: " + seatNo));
    }

    @Override
    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException {
        Section section = sectionRepository.findById(sectionNo)
                .orElseThrow(() -> new SectionNotFoundException("Section not found!"));

        // Validate capacity
        if (section.getSeats().size() >= section.getCapacity()) {
            throw new RuntimeException("Cannot add seat: Section capacity exceeded!");
        }

        seat.setSection(section);
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getAllSeats() throws SeatNotFoundException {

        List<Seat> list = seatRepository.findAll();
        if(list.isEmpty()) {
            throw new SeatNotFoundException("Seat not found!!!");
        }
        return list;
    }

    @Override
    public void selectSeats(int userId, int seatIds) throws RuntimeException {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No User Found!!"));
        Seat seat = getSeat(seatIds);

        if (seat.isBooked()) {
            throw new RuntimeException("Seat is already booked: " + seat.getSeatNumber());
        }

        if (!user.getSelectedSeatIds().contains(seat.getId())) {
            user.getSelectedSeatIds().add(seat.getId());
            userRepository.save(user);
        }
    }


}
