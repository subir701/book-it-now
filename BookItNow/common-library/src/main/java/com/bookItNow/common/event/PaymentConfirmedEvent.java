package com.bookItNow.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmedEvent {
    private int reservationId;
    private int userId;
    private double totalAmount;
}
