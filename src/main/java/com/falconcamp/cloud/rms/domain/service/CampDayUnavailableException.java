//: com.falconcamp.cloud.rms.domain.service.CampDayUnavailableException.java


package com.falconcamp.cloud.rms.domain.service;


import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;


@AllArgsConstructor(staticName = "of")
public class CampDayUnavailableException extends RuntimeException {

    private final List<OffsetDateTime> unavailableCampDays;

    public List<OffsetDateTime> getUnavailableCampDates() {
        return ImmutableList.copyOf(this.unavailableCampDays);
    }

    @Override
    public String getMessage() {
        return String.format("Unavailable Camp Days: %n",
                this.unavailableCampDays.toString());
    }

}///:~