package com.bookItNow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDetails {
    private int seatId;
    private String seatNumber;
    private double seatPrice;
}
