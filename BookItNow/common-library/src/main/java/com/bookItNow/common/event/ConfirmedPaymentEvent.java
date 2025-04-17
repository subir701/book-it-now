package com.bookItNow.common.event;

import lombok.Data;

@Data
public class ConfirmedPaymentEvent {

    private int reservationId;
    private int userId;
    private String paymentId;
    private boolean status;
}
