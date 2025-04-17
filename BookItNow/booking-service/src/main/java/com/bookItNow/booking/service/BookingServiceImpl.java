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
    public Booking createBooking(ConfirmedPaymentEvent event) {



        Reservation reservation = reservationRepository.findById(event.getReservationId()).orElseThrow(()->new ReservationNotFoundException("Reservation not found by this ID : "+ event.getReservationId()));

        Booking booking = new Booking();

        booking.setUserId(event.getUserId());
        double totalPrice = reservation.getSeatDetails().stream().mapToDouble(i -> i.getSeatPrice()).sum();
        booking.setTotalPrice(totalPrice);
        booking.setPaymentId(event.getPaymentId());

        List<Ticket> ticketList = reservation.getSeatDetails().stream().
                map(seatDetails -> {
                    Ticket ticket = new Ticket();
                    ticket.setBooking(booking);
                    ticket.setSeatNumber(seatDetails.getSeatNumber());
                    return ticket;
                }).collect(Collectors.toList());
        booking.setTickets(ticketList);

        ticketRepository.saveAll(ticketList);

        Booking temp = bookingRepository.save(booking);

        log.info("Booking created successfully");

        SeatStatusUpdateEvent seatStatusUpdateEvent = new SeatStatusUpdateEvent(
                reservation.getEventId(),
                temp.getId(),
                reservation.getSeatDetails().stream()
                        .map(SeatDetails::getSeatId)
                        .collect(Collectors.toList()),
                true
        );

        rabbitTemplate.convertAndSend("seat.exchange", "seat.status.update", seatStatusUpdateEvent);
        log.info("SeatStatusUpdateEvent sent to event-service for eventId: " + reservation.getEventId());

        return temp;

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
