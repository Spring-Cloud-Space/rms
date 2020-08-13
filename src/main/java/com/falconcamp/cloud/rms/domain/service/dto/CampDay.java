//: com.falconcamp.cloud.rms.domain.service.dto.CampDay.java


package com.falconcamp.cloud.rms.domain.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;


@Getter
@Setter
public final class CampDay implements ICampDay, Comparable<CampDay> {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private final OffsetDateTime day;

    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private final boolean reserved;

    private CampDay(OffsetDateTime day, boolean reserved) {
        this.day = day;
        this.reserved = reserved;
    }

    static ICampDay of(OffsetDateTime day) {
        return new CampDay(Objects.requireNonNull(day), false);
    }

    static ICampDay of(OffsetDateTime day, boolean reserved) {
        return new CampDay(Objects.requireNonNull(day), reserved);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampDay campDay = (CampDay) o;
        return Objects.equals(day, campDay.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

    @Override
    public boolean isAvailable() {
        return !this.reserved;
    }

    @Override
    public int compareTo(CampDay other) {
        return new CompareToBuilder()
                .append(this.day, other.day)
                .append(this.reserved, other.reserved)
                .toComparison();
    }

}///:~