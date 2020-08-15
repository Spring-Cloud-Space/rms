//: com.falconcamp.cloud.rms.domain.service.ReservationNotFoundException.java


package com.falconcamp.cloud.rms.domain.service;


import lombok.AllArgsConstructor;

import java.util.UUID;


@AllArgsConstructor(staticName = "of")
public class ReservationNotFoundException extends RuntimeException {

    private final UUID id;

    public String getMessage() {
        return String.format("Reservation '%s' doesn't exist.", this.id.toString());
    }

}///:~