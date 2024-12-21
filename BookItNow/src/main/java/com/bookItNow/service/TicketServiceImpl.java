package com.bookItNow.service;

import com.bookItNow.entity.Booking;
import com.bookItNow.entity.Ticket;
import com.bookItNow.repository.TicketRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketRepository ticketRepository;



    @Override
    public List<Ticket> findByBookingId(Integer bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Integer ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    @Transactional
    public boolean bookTicket(Integer ticketId, Integer bookingId) {
        int retryCount = 3; // Max number of retries for handling concurrent updates

        while (retryCount > 0) {
            try {
                // Fetch the ticket by ID
                Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
                if (!optionalTicket.isPresent()) {
                    throw new RuntimeException("Ticket not found with ID: " + ticketId);
                }

                Ticket ticket = optionalTicket.get();

                // Check if the ticket is already booked
                if (ticket.getBooking() != null) {
                    return false; // Ticket is already booked
                }

                // Assign the booking to the ticket
                Booking booking = new Booking();
                booking.setId(bookingId); // Assume booking object is pre-created
                ticket.setBooking(booking);

                // Save the updated ticket
                ticketRepository.save(ticket);

                return true; // Booking successful

            } catch (OptimisticLockException e) {
                retryCount--; // Retry if a conflict occurs
                if (retryCount == 0) {
                    throw new RuntimeException("Failed to book ticket due to concurrent updates. Please try again.");
                }
            }
        }

        return false;
    }
}
