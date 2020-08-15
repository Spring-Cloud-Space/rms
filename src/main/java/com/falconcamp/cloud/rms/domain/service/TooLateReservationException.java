//: com.falconcamp.cloud.rms.domain.service.TooLateReservationException.java


package com.falconcamp.cloud.rms.domain.service;


import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;


@AllArgsConstructor(staticName = "of")
public class TooLateReservationException extends RuntimeException {

    private final OffsetDateTime startDay;

    @Override
    public String getMessage() {
        return String.format("It's too late to reserve our Campsite for %s",
                this.startDay.toString());
    }

}///:~