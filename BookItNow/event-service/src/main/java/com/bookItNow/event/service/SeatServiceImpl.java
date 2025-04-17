package com.bookItNow.event.service;

import com.bookItNow.common.dto.SeatDetails;
import com.bookItNow.common.event.CancelReservationEvent;
import com.bookItNow.common.event.ReservationEvent;
import com.bookItNow.common.event.SeatStatusUpdateEvent;
import com.bookItNow.event.model.Event;
import com.bookItNow.event.model.Seat;
import com.bookItNow.event.model.Section;
import com.bookItNow.event.exception.EventNotFoundException;
import com.bookItNow.event.exception.SeatNotFoundException;
import com.bookItNow.event.exception.SectionNotFoundException;
import com.bookItNow.event.repository.EventRepository;
import com.bookItNow.event.repository.SeatRepository;
import com.bookItNow.event.repository.SectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeatServiceImpl implements SeatService {


    private final SeatRepository seatRepository;

    private final SectionRepository sectionRepository;

    private final EventRepository eventRepository;

    private final SeatLockService seatLockService;

    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    public SeatServiceImpl(SeatRepository seatRepository, SectionRepository sectionRepository, EventRepository eventRepository, SeatLockService seatLockService, KafkaTemplate<String, ReservationEvent> kafkaTemplate) {
        this.seatRepository = seatRepository;
        this.sectionRepository = sectionRepository;
        this.eventRepository = eventRepository;
        this.seatLockService = seatLockService;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public Seat getSeat(int seatNo) throws SeatNotFoundException {
        // Fetch a seat by its ID. If it doesn't exist, throw an exception.
        return seatRepository.findById(seatNo)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found for ID: " + seatNo));
    }

    @Override
    public Seat createSeat(Seat seat, Integer sectionNo) throws RuntimeException {
        // Fetch the section where the seat will be added.
        Section section = sectionRepository.findById(sectionNo)
                .orElseThrow(() -> new SectionNotFoundException("Section not found!"));

        // Check if the section's capacity allows adding a new seat.
        if (section.getSeats().size() >= section.getCapacity()) {
            throw new RuntimeException("Cannot add seat: Section capacity exceeded!");
        }

        // Associate the seat with the section and save it.
        seat.setSection(section);
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getAllSeats() throws SeatNotFoundException {
        // Fetch all seats from the database. If none exist, throw an exception.
        List<Seat> list = seatRepository.findAll();
        if (list.isEmpty()) {
            throw new SeatNotFoundException("Seat not found!!!");
        }
        return list;
    }

    @Override
    public void selectSeats(int userId,int eventId, List<Integer> seatIds) throws RuntimeException {

        List<SeatDetails> seatDetailsList = new ArrayList<>();
        for (Integer seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new SeatNotFoundException("Seat not found: " + seatId));

            // Lock the seat before processing
            if (!seatLockService.lockSeat(seatId)) {
                throw new RuntimeException("Seat already selected by another user.");
            }
            seatDetailsList.add(new SeatDetails(seatId,seat.getSeatNumber(),seat.getPrice()));

        }
        ReservationEvent event = new ReservationEvent(userId,eventId, seatDetailsList, "PENDING");
        kafkaTemplate.send("reservation.create", event);
    }

        @Override
        public List<Seat> getAllAvailableSeats(int eventId) throws SeatNotFoundException, EventNotFoundException {
            // Fetch the event by ID. If not found, throw an exception.
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event not found!"));

            // Extract all seats from the sections of the event and filter out booked ones.
            List<Seat> availableSeats = event.getSections().stream()
                    .flatMap(section -> section.getSeats().stream()) // Combine all seats from all sections.
                    .filter(seat -> !seat.isBooked()) // Keep only unbooked seats.
                    .collect(Collectors.toList());

            // If no available seats are found, throw an exception.
            if (availableSeats.isEmpty()) {
                throw new SeatNotFoundException("No available seats found for this event.");
            }

            return availableSeats;
        }

        @Override
        @KafkaListener(topics = "reservation.cancel", groupId = "event-service")
        public void cancelSelection(CancelReservationEvent cancelReservationEvent){

            Optional<Event> event = eventRepository.findById(cancelReservationEvent.getEventId());

            if(event.isPresent()){
                for(SeatDetails details : cancelReservationEvent.getSeats()){
                    seatLockService.unlockSeat(details.getSeatId());
                }
            }

        }

    @Override
    @RabbitListener(queues = "seat.status.update.queue")
    public void updateSeatStatus(SeatStatusUpdateEvent event)  {

        log.info("Update the seats status");

        event.getSeatIdList().forEach(seatId -> {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(()-> new SeatNotFoundException("Seat not found: "+seatId));
            seat.setBooked(true);
            seatRepository.save(seat);
        });
    }


}
