package com.bookItNow.payment.service;

import com.bookItNow.common.dto.PaymentRequest;

public interface PaymentService {

//    public String createPaymentIntent(Integer reservationId);
    public void processMockPayment(PaymentRequest paymentRequest);
}
