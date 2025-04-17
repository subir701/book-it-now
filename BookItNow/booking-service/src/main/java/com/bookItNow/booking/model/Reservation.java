package com.bookItNow.booking.model;


import com.bookItNow.common.dto.SeatDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private int eventId;

    @Column(columnDefinition = "jsonb") // Store as JSONB in Postgres
    private String seatDetails; // Renamed from seatIds

    private boolean isConfirmed;

    // Convert List<SeatDetail> to JSON before storing
    public void setSeatDetails(List<SeatDetails> seatDetailsList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.seatDetails = objectMapper.writeValueAsString(seatDetailsList);
        } catch (IOException e) {
            throw new RuntimeException("Error converting seat details to JSON", e);
        }
    }

    // Convert JSON back to List<SeatDetail> when retrieving
    public List<SeatDetails> getSeatDetails() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.seatDetails, new TypeReference<List<SeatDetails>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting seat details from JSON", e);
        }
    }

    @PrePersist
    public void prePersist() {
        this.isConfirmed = false;
    }
}

