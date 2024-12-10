package com.bookItNow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentMethod; // e.g., "Credit Card", "PayPal"

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean paymentStatus; // true if successful

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;


}
