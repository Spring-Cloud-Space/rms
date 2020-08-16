//: com.falconcamp.cloud.rms.domain.service.ReservationValidator.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;


@Component
final class ReservationTemporalValidator implements IReservationValidator {

    static final String TOO_LATE_MESSAGE =
            "It's too late to book your camp day(s)";

    static final String TOO_EARLY_MESSAGE =
            "It's too early to book your camp day(s); Only up to %s days in advance";

    static final String SATISFIED_MESSAGE =
            "It's satisfied to book your camp day(s)";

    static final int EARLIEST_ADVANCE_RESERVATION_DAYS = 30;

    @Override
    public Result validate(final ReservationDto reservationDto) {

        final ReservationDto dto = Objects.requireNonNull(reservationDto)
                .normalize();

        List<OffsetDateTime> bookDays = ICampDay.getBookedDays(dto);

        OffsetDateTime todayAsCampDay = ICampDay.normalize(OffsetDateTime.now());
        OffsetDateTime startDay = bookDays.get(0);

        Result result = Result.of(true, startDay, SATISFIED_MESSAGE);

        if (!todayAsCampDay.isBefore(startDay)) {
            result = Result.of(false, startDay, TOO_LATE_MESSAGE);
        } else if (DAYS.between(todayAsCampDay, startDay) >
                EARLIEST_ADVANCE_RESERVATION_DAYS) {
            result = Result.of(false, startDay, String.format(
                    TOO_EARLY_MESSAGE, EARLIEST_ADVANCE_RESERVATION_DAYS));
        }

        return result;
    }

    @AllArgsConstructor(staticName = "of")
    static class Result {

        private final boolean valid;
        private final OffsetDateTime startDate;
        private final String details;

        public boolean isValid() {
            return this.valid;
        }

        public String getResultInfo() {
            return String.format("%s : %s",
                    this.details, this.startDate.toString());
        }
    }

}///:~