//: com.falconcamp.cloud.rms.domain.service.ReservationNotFoundException.java


package com.falconcamp.cloud.rms.domain.service;


import lombok.AllArgsConstructor;

import java.util.UUID;


@AllArgsConstructor(staticName = "of")
public class ReservationNotFoundException extends RuntimeException {

    public static final String ERROR_MSG_TEMPLATE = "Reservation '%s' doesn't exist.";

    private final UUID id;

    public String getMessage() {
        return String.format(ERROR_MSG_TEMPLATE, this.id.toString());
    }

}///:~