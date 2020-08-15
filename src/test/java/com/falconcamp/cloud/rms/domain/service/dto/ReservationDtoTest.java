//: com.falconcamp.cloud.rms.domain.service.dto.ReservationDtoTest.java


package com.falconcamp.cloud.rms.domain.service.dto;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@DisplayName("DTO Test for Reservation - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationDtoTest {

    private UUID id;
    private long version;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;
    private String fullName;
    private String email;
    private OffsetDateTime startDateTime;
    private OffsetDateTime arrivalDateTime;
    private OffsetDateTime depatureDateTime;
    private int days;

    private ReservationDto dto;

    @BeforeEach
    void setUp() {

        OffsetDateTime today = OffsetDateTime.now();

        this.id = UUID.randomUUID();
        this.version = 1;
        this.createdDate = today;
        this.lastModifiedDate = today;
        this.fullName = RandomStringUtils.randomAlphanumeric(30);
        this.email = "random@unkown.com";
        this.startDateTime = today;
        this.arrivalDateTime = today.minusDays(1);
        this.days = MAX_RESERV_DAYS;
        this.depatureDateTime = today.plusDays(MAX_RESERV_DAYS);

        this.dto = ReservationDto.builder()
                .id(this.id)
                .version(this.version)
                .createdDate(this.createdDate)
                .lastModifiedDate(this.lastModifiedDate)
                .fullName(this.fullName)
                .email(this.email)
                .startDateTime(this.startDateTime)
                .arrivalDateTime(this.arrivalDateTime)
                .depatureDateTime(this.depatureDateTime)
                .days(this.days)
                .build();
    }

    @Test
    void test_ReservationDto_Can_Be_Normalized() {

        // Given
        OffsetDateTime expectedStartDay = ICampDay.normalize(this.startDateTime);
        OffsetDateTime expectedArrivalDay = ICampDay.normalize(this.arrivalDateTime);
        OffsetDateTime expectedDepatureDay = ICampDay.normalize(this.depatureDateTime);

        // When
        ReservationDto normalizedDto = this.dto.normalize();

        // Then
        assertThat(normalizedDto.getStartDateTime()).isEqualTo(expectedStartDay);
        assertThat(normalizedDto.getArrivalDateTime()).isEqualTo(expectedArrivalDay);
        assertThat(normalizedDto.getDepatureDateTime()).isEqualTo(expectedDepatureDay);

        assertThat(normalizedDto.getId()).isEqualTo(this.dto.getId());
        assertThat(normalizedDto.getVersion()).isEqualTo(this.dto.getVersion());
        assertThat(normalizedDto.getCreatedDate()).isEqualTo(this.dto.getCreatedDate());
        assertThat(normalizedDto.getLastModifiedDate()).isEqualTo(this.dto.getLastModifiedDate());
        assertThat(normalizedDto.getFullName()).isEqualTo(this.dto.getFullName());
        assertThat(normalizedDto.getEmail()).isEqualTo(this.dto.getEmail());
        assertThat(normalizedDto.getDays()).isEqualTo(this.dto.getDays());
    }

}///:~