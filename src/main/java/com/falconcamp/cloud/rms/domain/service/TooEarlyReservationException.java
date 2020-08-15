//: com.falconcamp.cloud.rms.domain.service.TooEarlyReservationException.java


package com.falconcamp.cloud.rms.domain.service;


import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;


@AllArgsConstructor(staticName = "of")
public class TooEarlyReservationException extends RuntimeException {

    private final OffsetDateTime startDay;

    @Override
    public String getMessage() {
        return String.format("It's too early to reserve our Campsite for %s. %nYou can reserve up to 30 days in advance.",
                this.startDay.toString());
    }

}///:~