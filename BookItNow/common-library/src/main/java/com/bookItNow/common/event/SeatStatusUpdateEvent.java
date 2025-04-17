package com.bookItNow.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusUpdateEvent {

    private int eventId;
    private int bookingId;
    private List<Integer> seatIdList;
    private boolean seatStatus;
}
