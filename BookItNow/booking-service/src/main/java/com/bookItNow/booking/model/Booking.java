package com.bookItNow.booking.model;

import com.bookItNow.common.Enum.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @Column(nullable = false)
    @Min(value = 0)
    private double totalPrice;

    @Column(nullable = false)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    private Status bookingStatus;

    @PrePersist
    public void prePersist() {
        bookingTime = LocalDateTime.now();
    }
}
