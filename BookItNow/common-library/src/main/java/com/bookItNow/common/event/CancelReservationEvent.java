package com.bookItNow.common.event;

import com.bookItNow.common.dto.SeatDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationEvent {

    private int userId;
    private int eventId;
    private List<SeatDetails> seats;
}
