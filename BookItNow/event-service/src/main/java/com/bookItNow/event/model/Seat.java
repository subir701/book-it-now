package com.bookItNow.event.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String seatNumber; // e.g., "A1", "B2"

    @Column(nullable = false)
    private boolean isBooked = false;

    @Column(nullable = false)
    @Min(value = 0)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private Section section;

    @Column(nullable = true)
    private Long bookingId;

    @Version
    private int version;


}
