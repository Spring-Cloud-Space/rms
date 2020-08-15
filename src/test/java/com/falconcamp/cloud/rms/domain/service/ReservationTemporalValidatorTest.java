//: com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidatorTest.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidator.Result;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidator.*;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@DisplayName("Reservation Validator Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationTemporalValidatorTest {

    private OffsetDateTime today;
    private ReservationDto dto;
    private ReservationTemporalValidator validator;

    @BeforeEach
    void setUp() {
        this.today = OffsetDateTime.now();
        this.validator = new ReservationTemporalValidator();
    }

    @Test
    void test_Can_Reserve_Mminimum_One_Day_Ahead_Of_Arrival() {

        // Given
        this.dto = ReservationDto.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .createdDate(this.today)
                .lastModifiedDate(this.today)
                .fullName(RandomStringUtils.randomAlphanumeric(30))
                .email("unknown@noname.com")
                .startDateTime(this.today.plusDays(1))
                .arrivalDateTime(this.today)
                .depatureDateTime(this.today.plusDays(1))
                .days(1)
                .build();

        // When
        Result result = this.validator.validate(this.dto);

        // Then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getResultInfo())
                .contains(ReservationTemporalValidator.SATISFIED_MESSAGE,
                        ICampDay.normalize(dto.getStartDateTime()).toString());
    }

    @Test
    void test_Can_Not_Reserve_In_The_Same_Day() {

        // Given
        this.dto = ReservationDto.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .createdDate(this.today)
                .lastModifiedDate(this.today)
                .fullName(RandomStringUtils.randomAlphanumeric(30))
                .email("unknown@noname.com")
                .startDateTime(this.today)
                .arrivalDateTime(this.today)
                .depatureDateTime(this.today.plusDays(1))
                .days(1)
                .build();

        // When
        Result result = this.validator.validate(this.dto);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getResultInfo()).contains(TOO_LATE_MESSAGE,
                ICampDay.normalize(dto.getStartDateTime()).toString());
    }

    @Test
    void test_Can_Not_Reserve_Up_To_30_Days_In_Advance() {

        // Given
        OffsetDateTime startDay = today.plusDays(EARLIEST_ADVANCE_RESERVATION_DAYS + 1);

        this.dto = ReservationDto.builder()
                .id(UUID.randomUUID())
                .version(1L)
                .createdDate(this.today)
                .lastModifiedDate(startDay)
                .fullName(RandomStringUtils.randomAlphanumeric(30))
                .email("unknown@noname.com")
                .startDateTime(startDay)
                .arrivalDateTime(startDay)
                .depatureDateTime(startDay)
                .days(1)
                .build();

        // When
        Result result = this.validator.validate(this.dto);

        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getResultInfo()).contains("" +
                        EARLIEST_ADVANCE_RESERVATION_DAYS,
                        ICampDay.normalize(dto.getStartDateTime()).toString());
    }

}///:~