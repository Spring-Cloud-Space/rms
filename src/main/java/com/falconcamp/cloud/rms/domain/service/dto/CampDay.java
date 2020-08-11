//: com.falconcamp.cloud.rms.domain.service.dto.CampDay.java


package com.falconcamp.cloud.rms.domain.service.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.OffsetDateTime;
import java.util.Objects;


@Getter
@Setter
@EqualsAndHashCode
public class CampDay implements ICampDay, Comparable<CampDay> {

    private final OffsetDateTime day;
    private final boolean reserved;

    private CampDay(OffsetDateTime day, boolean reserved) {
        this.day = day;
        this.reserved = reserved;
    }

    public static CampDay of(OffsetDateTime day) {
        return new CampDay(Objects.requireNonNull(day), false);
    }

    public static CampDay of(OffsetDateTime day, boolean reserved) {
        return new CampDay(Objects.requireNonNull(day), reserved);
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