package com.bookItNow.booking.service;

import com.bookItNow.booking.exception.ReservationNotFoundException;
import com.bookItNow.booking.model.Reservation;
import com.bookItNow.booking.repository.ReservationRepository;
import com.bookItNow.common.event.CancelReservationEvent;
import com.bookItNow.common.event.ReservationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, CancelReservationEvent> kafkaTemplate;

    public ReservationServiceImpl(ReservationRepository reservationRepository, KafkaTemplate<String, CancelReservationEvent> kafkaTemplate) {
        this.reservationRepository = reservationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @KafkaListener(topics = "reservation.create", groupId = "booking-service")
    public void createReservation(ReservationEvent reservationEvent) {
        log.info("Reservation object Intalized");
        Reservation reservation = new Reservation();
        reservation.setUserId(reservationEvent.getUserId());
        reservation.setEventId(reservationEvent.getEventId());
        reservation.setSeatDetails(reservationEvent.getSeatDetailsList());

        log.info("Reservation created");

        reservationRepository.save(reservation);
    }

    @Override
    public Reservation findReservation(Integer reservationId)throws ReservationNotFoundException {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation not found by ID "+reservationId));
    }

    @Override
    public void cancelReservation(Integer reservationId) throws ReservationNotFoundException {
        log.info("Getting reservation object by it ID");
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation not found by ID "+reservationId));

        log.info("Cancel Reservation Event Created");

        CancelReservationEvent event = new CancelReservationEvent(reservation.getUserId(),reservation.getEventId(),reservation.getSeatDetails());

        log.info("Produce cancel reservation event");

        kafkaTemplate.send("reservation.cancel",event);

        log.info("Reservation Canceled");

        reservationRepository.delete(reservation);


    }


}
