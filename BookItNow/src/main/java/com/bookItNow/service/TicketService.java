package com.bookItNow.service;

import com.bookItNow.entity.Ticket;
import com.bookItNow.repository.TicketRepository;

import java.util.List;

public interface TicketService {

    List<Ticket> findByEventId(Integer eventId);
    List<Ticket> findByBookingId(Integer bookingId);
    Ticket createTicket(Ticket ticket);
    void deleteTicket(Integer ticketId);
    public boolean bookTicket(Integer ticketId, Integer bookingId);
}
