package com.bookItNow.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventSummaryDTO {
    private Integer id;
    private String name;
    private String venue;
    private Long availableSeatsCount; // The database will calculate this
    private Double startingPrice;     // The database will find the minimum
}