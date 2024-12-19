package com.bookItNow.service;

import com.bookItNow.entity.Booking;
import com.bookItNow.entity.Seat;
import com.bookItNow.entity.Ticket;
import com.bookItNow.entity.User;
import com.bookItNow.repository.BookingRepository;
import com.bookItNow.repository.SeatRepository;
import com.bookItNow.repository.TicketRepository;
import com.bookItNow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Booking createBooking(Integer userId) {

        User user = userRepository.findById(userId).get();

        // Fetch selected seats
        List<Integer> seatIds = user.getSelectedSeatIds();
        if (seatIds.isEmpty()) {
            throw new RuntimeException("No seats selected for booking.");
        }

        // Fetch seats from repository and validate availability
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats are invalid or already booked.");
        }

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setUser(user);

        if(booking.getTickets() == null) {
            booking.setTickets(new ArrayList<>());
        }

        double totalAmount = 0.0;

        // Mark seats as booked
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setBooking(booking);
            booking.getTickets().add(ticket);
            seat.setBooked(true);
            totalAmount+=seat.getPrice();
            ticketRepository.save(ticket);
            seatRepository.save(seat); // Update seat status
        }

        booking.setTotalPrice(totalAmount);
        booking.setBookingTime(LocalDateTime.now());
        // Save booking
        user.getBookings().add(booking);
        user.setSelectedSeatIds(new ArrayList<>()); // Clear selected seats after booking
        userRepository.save(user);

        return bookingRepository.save(booking);

    }

    @Override
    public List<Booking> findByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public void deleteBooking(Integer bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}
