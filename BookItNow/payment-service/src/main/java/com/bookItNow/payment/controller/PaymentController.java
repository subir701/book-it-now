package com.bookItNow.payment.controller;


import com.bookItNow.common.dto.PaymentRequest;
import com.bookItNow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookitnow/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private PaymentService paymentService;

//    public ResponseEntity<String> createPayment(@PathVariable Integer reservationId){
//        String clientSecert = paymentService.createPaymentIntent(reservationId);
//        return ResponseEntity.ok(clientSecert);
//    }

    @PostMapping("/pay")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest paymentRequest){
        log.info("Going to complete payment and receving payment request");

        paymentService.processMockPayment(paymentRequest);

        log.info("Completed Payement of totoal payment: {} ", paymentRequest.getAmount());

        return ResponseEntity.ok("Payment processed successfully");
    }
}
