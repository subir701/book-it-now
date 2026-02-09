package com.bookItNow.booking.service;

import com.bookItNow.booking.exception.ReservationNotFoundException;
import com.bookItNow.booking.model.Booking;
import com.bookItNow.booking.model.Reservation;
import com.bookItNow.booking.model.Ticket;
import com.bookItNow.booking.repository.ReservationRepository;
import com.bookItNow.common.dto.SeatDetails;
import com.bookItNow.common.event.ConfirmedPaymentEvent;
import com.bookItNow.booking.repository.BookingRepository;
import com.bookItNow.booking.repository.TicketRepository;
import com.bookItNow.common.event.SeatStatusUpdateEvent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final TicketRepository ticketRepository;

    private final ReservationRepository reservationRepository;

    private final RabbitTemplate rabbitTemplate;

    public BookingServiceImpl(BookingRepository bookingRepository, TicketRepository ticketRepository, ReservationRepository reservationRepository, RabbitTemplate rabbitTemplate) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    @RabbitListener(queues = "payment.confirmed.queue")
    public void createBooking(ConfirmedPaymentEvent event) {
        log.info("Processing confirmed payment for reservation: {}", event.getReservationId());

        // 1. Prevent duplicate bookings
        if (bookingRepository.existsByPaymentId(event.getPaymentId())) {
            log.warn("Booking already exists for paymentId: {}", event.getPaymentId());
            return;
        }

        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        // 2. Map Reservation to Booking
        Booking booking = new Booking();
        booking.setUserId(event.getUserId());
        booking.setPaymentId(event.getPaymentId());
        booking.setTotalPrice(reservation.getSeatDetails().stream().mapToDouble(SeatDetails::getSeatPrice).sum());

        List<Ticket> tickets = reservation.getSeatDetails().stream().map(seat -> {
            Ticket t = new Ticket();
            t.setBooking(booking);
            t.setSeatNumber(seat.getSeatNumber());
            return t;
        }).collect(Collectors.toList());

        booking.setTickets(tickets);

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Update the Event Service (KAFKA - to match the listener you wrote)
        SeatStatusUpdateEvent updateEvent = new SeatStatusUpdateEvent(
                reservation.getEventId(),
                savedBooking.getId(),
                reservation.getSeatDetails().stream().map(SeatDetails::getSeatId).collect(Collectors.toList()),
                true
        );

        rabbitTemplate.convertAndSend("seat.exchange", "seat.status.update", updateEvent);
        log.info("SeatStatusUpdateEvent sent to event-service for eventId: " + reservation.getEventId());

        reservationRepository.delete(reservation);

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
