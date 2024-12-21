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

        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Fetch selected seats
        List<Integer> seatIds = user.getSelectedSeatIds();
        if (seatIds.isEmpty()) {
            throw new RuntimeException("No seats selected for booking.");
        }

        // Fetch seats from the repository and validate their availability
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

        // Initialize tickets collection if null
        if (booking.getTickets() == null) {
            booking.setTickets(new ArrayList<>());
        }

        // Save the booking first
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(0.0); // Placeholder for total price
        Booking savedBooking = bookingRepository.save(booking);

        // Calculate total price and add tickets
        double totalAmount = 0.0;
        for (Seat seat : seats) {
            // Create and associate tickets
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setBooking(savedBooking); // Associate with the saved booking
            booking.getTickets().add(ticket);

            // Mark the seat as booked
            seat.setBooked(true);
            seatRepository.save(seat); // Update seat status
            totalAmount += seat.getPrice();
            
        }

        // Save tickets in bulk
        ticketRepository.saveAll(booking.getTickets());

        // Update the booking with total price
        savedBooking.setTotalPrice(totalAmount);

        // Update user's bookings
        user.getBookings().add(savedBooking);
        user.setSelectedSeatIds(new ArrayList<>()); // Clear selected seats after booking
        userRepository.save(user);

        // Save and return the final booking
        return bookingRepository.save(savedBooking);
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
