package com.bookItNow.booking.service;

import com.bookItNow.booking.model.Booking;
import com.bookItNow.booking.model.Ticket;
import com.bookItNow.booking.repository.TicketRepository;
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

    /**
     * Finds all tickets associated with a specific booking ID.
     *
     * @param bookingId The ID of the booking.
     * @return List of tickets related to the booking.
     */
    @Override
    public List<Ticket> findByBookingId(Integer bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }

    /**
     * Creates and saves a new ticket in the database.
     *
     * @param ticket The ticket to be created.
     * @return The saved ticket.
     */
    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /**
     * Deletes a ticket by its ID.
     *
     * @param ticketId The ID of the ticket to be deleted.
     */
    @Override
    public void deleteTicket(Integer ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    /**
     * Books a ticket by associating it with a booking ID. Handles concurrent booking attempts using retry logic.
     *
     * @param ticketId  The ID of the ticket to be booked.
     * @param bookingId The ID of the booking to associate with the ticket.
     * @return True if the booking was successful, false if the ticket was already booked.
     * @throws RuntimeException if booking fails due to concurrent updates after retries.
     */
    @Override
    @Transactional
    public boolean bookTicket(Integer ticketId, Integer bookingId) {
        int retryCount = 3; // Maximum number of retries for handling concurrent updates.

        while (retryCount > 0) {
            try {
                // Fetch the ticket by its ID.
                Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
                if (!optionalTicket.isPresent()) {
                    throw new RuntimeException("Ticket not found with ID: " + ticketId);
                }

                Ticket ticket = optionalTicket.get();

                // Check if the ticket is already booked.
                if (ticket.getBooking() != null) {
                    return false; // Ticket is already booked.
                }

                // Create a booking object and assign it to the ticket.
                Booking booking = new Booking();
                booking.setId(bookingId); // Assume the booking object is pre-created elsewhere.
                ticket.setBooking(booking);

                // Save the updated ticket with the booking association.
                ticketRepository.save(ticket);

                return true; // Booking was successful.

            } catch (OptimisticLockException e) {
                // Handle concurrent update conflict using retry logic.
                retryCount--;
                if (retryCount == 0) {
                    throw new RuntimeException("Failed to book ticket due to concurrent updates. Please try again.");
                }
            }
        }

        return false; // Should never reach here under normal conditions.
    }
}
