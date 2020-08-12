//: com.falconcamp.cloud.rms.domain.service.dto.ReservationDto.java


package com.falconcamp.cloud.rms.domain.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.ReservationService.MIN_RESERV_DAYS;
import static com.falconcamp.cloud.rms.domain.service.ReservationService.MAX_RESERV_DAYS;


@Data
@NoArgsConstructor
@Builder @AllArgsConstructor
public class ReservationDto implements Serializable {

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
    private OffsetDateTime detatureDateTime;

    @Min(MIN_RESERV_DAYS)
    @Max(MAX_RESERV_DAYS)
    private int days;

}///:~