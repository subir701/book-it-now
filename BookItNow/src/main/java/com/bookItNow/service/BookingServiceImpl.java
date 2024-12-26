package com.bookItNow.service;

import com.bookItNow.model.Booking;
import com.bookItNow.model.Seat;
import com.bookItNow.model.Ticket;
import com.bookItNow.model.User;
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

        // Step 1: Fetch the user making the booking
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Step 2: Fetch the user's selected seats
        List<Integer> seatIds = user.getSelectedSeatIds();
        if (seatIds.isEmpty()) {
            throw new RuntimeException("No seats selected for booking."); // Simple validation
        }

        // Step 3: Validate the selected seats
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats are invalid or already booked."); // Prevent mismatches
        }

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber()); // Avoid double booking
            }
        }

        // Step 4: Create a new booking object and associate it with the user
        Booking booking = new Booking();
        booking.setUser(user);

        // Initialize the tickets collection if it's null
        if (booking.getTickets() == null) {
            booking.setTickets(new ArrayList<>());
        }

        // Save the booking to ensure we have a valid booking ID before linking tickets
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(0.0); // Temporary placeholder for total price
        Booking savedBooking = bookingRepository.save(booking);

        // Step 5: Add tickets for each selected seat and calculate the total amount
        double totalAmount = 0.0;
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setSeat(seat);
            ticket.setBooking(savedBooking); // Associate the ticket with the saved booking
            booking.getTickets().add(ticket); // Add ticket to the booking

            seat.setBooked(true); // Mark the seat as booked
            seatRepository.save(seat); // Save the updated seat status
            totalAmount += seat.getPrice(); // Update the total price
        }

        // Save all tickets in one go for efficiency
        ticketRepository.saveAll(booking.getTickets());

        // Step 6: Update the booking with the calculated total price
        savedBooking.setTotalPrice(totalAmount);

        // Update the user's bookings and clear selected seats
        user.getBookings().add(savedBooking);
        user.setSelectedSeatIds(new ArrayList<>()); // Clear selected seat IDs
        userRepository.save(user); // Save the updated user

        // Step 7: Save the final booking and return it
        return bookingRepository.save(savedBooking);
    }

    @Override
    public List<Booking> findByUserId(Integer userId) {
        // Simple method to fetch all bookings for a specific user
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public void deleteBooking(Integer bookingId) {
        // Straightforward deletion of a booking by ID
        bookingRepository.deleteById(bookingId);
    }
}
