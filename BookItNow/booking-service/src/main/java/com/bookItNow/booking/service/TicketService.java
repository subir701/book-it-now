package com.bookItNow.booking.service;

import com.bookItNow.booking.model.Ticket;

import java.util.List;

public interface TicketService {

    List<Ticket> findByBookingId(Integer bookingId);
    Ticket createTicket(Ticket ticket);
    void deleteTicket(Integer ticketId);
    public boolean bookTicket(Integer ticketId, Integer bookingId);
}
