package com.bookItNow.user.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service", url = "http://localhost:8081")
public interface BookingClient {

    @GetMapping("/booking/user/{userId}")
    List<String> getUserBookings(@PathVariable("userId") int userId);
}
