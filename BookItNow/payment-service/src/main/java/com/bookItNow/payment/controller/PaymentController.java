package com.bookItNow.payment.controller;


import com.bookItNow.common.dto.PaymentRequest;
import com.bookItNow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookitnow/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private PaymentService paymentService;

//    public ResponseEntity<String> createPayment(@PathVariable Integer reservationId){
//        String clientSecert = paymentService.createPaymentIntent(reservationId);
//        return ResponseEntity.ok(clientSecert);
//    }

    @PostMapping("/pay")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest paymentRequest){
        paymentService.processMockPayment(paymentRequest);
        return ResponseEntity.ok("Payment processed successfully");
    }
}
