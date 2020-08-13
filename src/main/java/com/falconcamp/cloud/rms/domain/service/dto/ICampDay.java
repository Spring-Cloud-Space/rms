//: com.falconcamp.cloud.rms.domain.service.dto.ICampDay.java


package com.falconcamp.cloud.rms.domain.service.dto;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;


public interface ICampDay {

    public static final ZoneOffset DEFAULT_ZONE_OFFSET =
            OffsetDateTime.now().getOffset();

    public static final int MIN_RESERV_DAYS = 1;
    public static final int MAX_RESERV_DAYS = 3;
    public static final int DEFAULT_SEARCH_MONTHS = 1;

    OffsetDateTime getDay();
    boolean isReserved();
    boolean isAvailable();

    static ICampDay ofAvailable(OffsetDateTime day) {
        return CampDay.of(Objects.requireNonNull(day), false);
    }

    static ICampDay ofReserved(OffsetDateTime day) {
        return CampDay.of(Objects.requireNonNull(day), true);
    }

    static OffsetDateTime asStartDay(OffsetDateTime any) {
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

    static OffsetDateTime asSearchFromDay(OffsetDateTime any) {
        OffsetDateTime from = any.minusDays(MAX_RESERV_DAYS);
        return OffsetDateTime.of(
                from.getYear(), from.getMonthValue(), from.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

    static OffsetDateTime getSearchEndDay(OffsetDateTime any, int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("[SEARCH] - Illegal months.");
        }
        OffsetDateTime end = any.plusMonths(months);
        return OffsetDateTime.of(
                end.getYear(), end.getMonthValue(), end.getDayOfMonth(),
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);
    }

}///:~