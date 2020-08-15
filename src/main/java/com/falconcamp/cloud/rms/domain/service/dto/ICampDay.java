//: com.falconcamp.cloud.rms.domain.service.dto.ICampDay.java


package com.falconcamp.cloud.rms.domain.service.dto;


import com.falconcamp.cloud.rms.domain.service.IllegalSearchArgumentsException;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;


public interface ICampDay {

    static final String ILLEGAL_SEARCH_BOUNDRY_ERROR_MESSAGE = "Illegal search boundry.";

    public static final ZoneOffset DEFAULT_ZONE_OFFSET =
            OffsetDateTime.now().getOffset();

    public static final int MIN_RESERV_DAYS = 1;
    public static final int MAX_RESERV_DAYS = 3;
    public static final int DEFAULT_SEARCH_MONTHS = 1;

    OffsetDateTime getDay();
    boolean isReserved();
    boolean isAvailable();

    static ICampDay ofAvailable(@NonNull OffsetDateTime day) {
        return CampDay.of(day, false);
    }

    static ICampDay ofReserved(@NonNull OffsetDateTime day) {
        return CampDay.of(day, true);
    }

    static OffsetDateTime normalize(@NonNull OffsetDateTime any) {
        return OffsetDateTime.of(
                any.getYear(), any.getMonthValue(), any.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime fromTomorrow() {
        OffsetDateTime tomorrow = OffsetDateTime.now().plusDays(1);
        return OffsetDateTime.of(
                tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime asSearchFromDay(@NonNull OffsetDateTime any) {
        OffsetDateTime from = any.minusDays(MAX_RESERV_DAYS);
        return OffsetDateTime.of(
                from.getYear(), from.getMonthValue(), from.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime getSearchEndDay(@NonNull OffsetDateTime any, int months) {

        if (months <= 0) {
            months = DEFAULT_SEARCH_MONTHS;
        }
        OffsetDateTime end = any.plusMonths(months);
        return OffsetDateTime.of(
                end.getYear(), end.getMonthValue(), end.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime asFromDay(LocalDate date) {
        return Objects.isNull(date) ? fromTomorrow() : OffsetDateTime.of(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime calcEndDay(OffsetDateTime from, LocalDate to) {

        if (Objects.isNull(from)) {
            from = fromTomorrow();
        }

        if (Objects.isNull(to)) {
            return getSearchEndDay(from, DEFAULT_SEARCH_MONTHS);
        }

        OffsetDateTime endDay = OffsetDateTime.of(
                to.getYear(), to.getMonthValue(), to.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);

        if (endDay.isBefore(from)) {
            throw IllegalSearchArgumentsException.of(
                    from, endDay, ILLEGAL_SEARCH_BOUNDRY_ERROR_MESSAGE);
        }

        return endDay;
    }

}///:~