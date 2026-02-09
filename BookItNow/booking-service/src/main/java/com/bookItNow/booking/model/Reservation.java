package com.bookItNow.booking.model;


import com.bookItNow.booking.converter.SeatDetailsConverter;
import com.bookItNow.common.Enum.Status;
import com.bookItNow.common.dto.SeatDetails;
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

    @Convert(converter = SeatDetailsConverter.class)
    @Column(columnDefinition = "jsonb")
    private List<SeatDetails> seatDetails;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @PrePersist
    public void prePersist() {
        if(this.status == null){
            this.status = Status.PENDING;
        }
    }
}

