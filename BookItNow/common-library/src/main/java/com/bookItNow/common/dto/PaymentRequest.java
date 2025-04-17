package com.bookItNow.common.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private int reservationId;
    private int userId;
    private double amount;
}
