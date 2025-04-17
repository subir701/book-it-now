package com.bookItNow.payment.service;

import com.bookItNow.common.dto.PaymentRequest;
import com.bookItNow.common.dto.ReservationDTO;
import com.bookItNow.common.dto.SeatDetails;
import com.bookItNow.common.event.ConfirmedPaymentEvent;
import com.bookItNow.common.event.PaymentConfirmedEvent;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

//    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;


//    public String createPaymentIntent(Integer reservationId){
//        ReservationDTO reservation = restTemplate.getForObject(
//                "http://BOOKING-SERVICE/bookitnow/v1/reservations/get/" + reservationId,
//                ReservationDTO.class
//        );
//
//        // Step 2: Calculate total price
//        double total = reservation.getSeatDetailsList().stream()
//                .mapToDouble(SeatDetails::getSeatPrice)
//                .sum();
//
//        // Step 3: Create Stripe Payment Intent
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("amount", (int) (total * 100)); // Stripe expects amount in cents
//            params.put("currency", "inr");
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            // Step 4: Send PaymentConfirmedEvent to Booking Service
//            PaymentConfirmedEvent event = new PaymentConfirmedEvent(
//                    reservation.getReservationId(),
//                    reservation.getUserId(),
//                    total
//            );
//
//            rabbitTemplate.convertAndSend("payment.exchange", "payment.success", event);
//
//            return paymentIntent.getClientSecret();
//
//        } catch (StripeException e) {
//            throw new RuntimeException("Payment failed: " + e.getMessage());
//        }
//    }

    public void processMockPayment(PaymentRequest paymentRequest){

        log.info("Mock payment success for userId: "+paymentRequest.getUserId());
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){}

        ConfirmedPaymentEvent event = new ConfirmedPaymentEvent();

        event.setReservationId(paymentRequest.getReservationId());
        event.setUserId(paymentRequest.getUserId());
        event.setPaymentId(UUID.randomUUID().toString());
        event.setStatus(true);

        rabbitTemplate.convertAndSend("payment.exchange","payment.confirmed", event);
        log.info("ConfirmedPaymentEvent sent to booking service for reservationId: "+paymentRequest.getReservationId());
    }

}
