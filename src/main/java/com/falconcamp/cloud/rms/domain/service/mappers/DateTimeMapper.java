//: com.falconcamp.cloud.rms.domain.service.mappers.DateTimeMapper.java


package com.falconcamp.cloud.rms.domain.service.mappers;


import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.DEFAULT_ZONE_OFFSET;


@Component
public class DateTimeMapper {

    public OffsetDateTime asOffsetDateTime(final Timestamp ts) {

        if (Objects.isNull(ts)) {
            return null;
        }

        return OffsetDateTime.of(ts.toLocalDateTime().getYear(),
                ts.toLocalDateTime().getMonthValue(),
                ts.toLocalDateTime().getDayOfMonth(),
                ts.toLocalDateTime().getHour(),
                ts.toLocalDateTime().getMinute(),
                ts.toLocalDateTime().getSecond(),
                ts.toLocalDateTime().getNano(),
                DEFAULT_ZONE_OFFSET);
    }

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {

        if (Objects.isNull(offsetDateTime)) {
            return null;
        }

        LocalDateTime localDateTime = offsetDateTime.atZoneSameInstant(
                DEFAULT_ZONE_OFFSET).toLocalDateTime();

        return Timestamp.valueOf(localDateTime);
    }

}///:~