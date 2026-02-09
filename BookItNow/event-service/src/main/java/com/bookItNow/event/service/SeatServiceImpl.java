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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // Replaced manual constructor with Lombok
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;
    private final EventRepository eventRepository;
    private final SeatLockService seatLockService;
    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    @Override
    public Seat getSeat(int seatNo) throws SeatNotFoundException {
        log.info("Fetching seat by ID : {}", seatNo);
        return seatRepository.findById(seatNo)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found for ID: " + seatNo));
    }

    /**
     * Optimized selectSeats:
     * 1. Uses Redisson for Distributed Locking.
     * 2. Updates DB status to prevent local race conditions.
     * 3. Publishes to Kafka within a transaction.
     */
    @Override
    @Transactional
    public void selectSeats(int userId, int eventId, List<Integer> seatIds) throws RuntimeException {
        log.info("User {} attempting to select seats {} for event {}", userId, seatIds, eventId);

        // 1. Acquire Distributed Locks via Redisson
        boolean isLocked = seatLockService.lockSeats(seatIds);
        if (!isLocked) {
            log.error("Failed to acquire locks for seats: {}", seatIds);
            throw new RuntimeException("One or more seats are currently being held by another user.");
        }

        try {
            List<SeatDetails> seatDetailsList = new ArrayList<>();
            List<Seat> seatsToUpdate = new ArrayList<>();

            for (Integer seatId : seatIds) {
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new SeatNotFoundException("Seat not found: " + seatId));

                // 2. Double-Check DB Status (Source of Truth)
                if (seat.isBooked()) {
                    throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already booked.");
                }

                // 3. Mark as PENDING (isBooked = true)
                seat.setBooked(true);
                seatsToUpdate.add(seat);
                seatDetailsList.add(new SeatDetails(seatId, seat.getSeatNumber(), seat.getPrice()));
            }

            // 4. Save to DB first (Triggers @Version check)
            seatRepository.saveAll(seatsToUpdate);

            // 5. Send to Kafka
            ReservationEvent event = new ReservationEvent(userId, eventId, seatDetailsList, "PENDING");
            kafkaTemplate.send("reservation.create", event);
            log.info("Reservation event sent to Kafka for user {}", userId);

        } catch (Exception e) {
            // 6. If DB or Kafka fails, release Redisson locks so others can try
            log.error("Error during seat selection, releasing Redis locks for seats: {}", seatIds);
            seatLockService.unlockSeats(seatIds);
            throw e;
        }
    }

    /**
     * Triggered when Booking/Payment times out or fails.
     * Resets DB status and releases Redis locks.
     */
    @Override
    @KafkaListener(topics = "reservation.cancel", groupId = "event-service")
    @Transactional
    public void cancelSelection(CancelReservationEvent cancelReservationEvent) {
        log.info("Processing seat cancellation for Event ID: {}", cancelReservationEvent.getEventId());

        List<Integer> seatIds = cancelReservationEvent.getSeats().stream()
                .map(SeatDetails::getSeatId)
                .collect(Collectors.toList());

        // 1. Reset Database Status
        List<Seat> seats = seatRepository.findAllById(seatIds);
        seats.forEach(seat -> seat.setBooked(false));
        seatRepository.saveAll(seats);

        // 2. Release Redis Locks
        seatLockService.unlockSeats(seatIds);
        log.info("Successfully cancelled selection and released locks for seats: {}", seatIds);
    }

    /**
     * Final confirmation from Payment Service.
     * Here we make the seat booking permanent.
     */
    @Override
    @RabbitListener(queues = "seat.status.update.queue")
    @Transactional
    public void updateSeatStatus(SeatStatusUpdateEvent event) {
        log.info("Confirming final booking for seats: {}", event.getSeatIdList());

        List<Seat> seats = seatRepository.findAllById(event.getSeatIdList());
        seats.forEach(seat -> {
            seat.setBooked(true); // Permanent status
            // Logic: In a real system, you'd set status = 'CONFIRMED'
        });
        seatRepository.saveAll(seats);

        // Release the Redis Lock because the DB is now the final authority
        seatLockService.unlockSeats(event.getSeatIdList());
    }

    @Override
    public List<Seat> getAllAvailableSeats(int eventId) throws SeatNotFoundException, EventNotFoundException {
        log.info("Fetching available seats for event ID {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found!"));

        return event.getSections().stream()
                .flatMap(section -> section.getSeats().stream())
                .filter(seat -> !seat.isBooked())
                .collect(Collectors.toList());
    }

    // --- Admin methods ---
    @Override
    public Seat createSeat(Seat seat, Integer sectionNo) {
        Section section = sectionRepository.findById(sectionNo)
                .orElseThrow(() -> new SectionNotFoundException("Section not found!"));
        seat.setSection(section);
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }
}