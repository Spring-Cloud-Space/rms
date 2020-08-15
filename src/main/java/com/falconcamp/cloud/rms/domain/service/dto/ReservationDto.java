//: com.falconcamp.cloud.rms.domain.service.dto.ReservationDto.java


package com.falconcamp.cloud.rms.domain.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableList;
import lombok.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.MAX_RESERV_DAYS;
import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.MIN_RESERV_DAYS;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder @AllArgsConstructor
public class ReservationDto implements Comparable<ReservationDto>, Serializable {

    @Null
    private UUID id;

    @Null
    private Long version;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastModifiedDate;

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime startDateTime;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime arrivalDateTime;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime depatureDateTime;

    @Min(MIN_RESERV_DAYS)
    @Max(MAX_RESERV_DAYS)
    private int days;

    public List<OffsetDateTime> getCampDates() {
        return IntStream.range(0, this.days)
                .mapToObj(i -> this.startDateTime.plusDays(i))
                .collect(ImmutableList.toImmutableList());
    }

    public ReservationDto normalize() {
        return ReservationDto.builder()
                .id(this.id)
                .version(this.version)
                .createdDate(this.createdDate)
                .lastModifiedDate(this.lastModifiedDate)
                .fullName(this.fullName)
                .email(this.email)
                .startDateTime(ICampDay.normalize(this.startDateTime))
                .arrivalDateTime(ICampDay.normalize(this.arrivalDateTime))
                .depatureDateTime(ICampDay.normalize(this.depatureDateTime))
                .days(this.days)
                .build();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ReservationDto)) {
            return false;
        }

        final ReservationDto other = (ReservationDto) obj;

        return new EqualsBuilder()
                .append(this.startDateTime, other.startDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.startDateTime).toHashCode();
    }

    @Override
    public int compareTo(final ReservationDto obj) {
        return new CompareToBuilder()
                .append(this.startDateTime, obj.startDateTime)
                .toComparison();
    }

}///:~