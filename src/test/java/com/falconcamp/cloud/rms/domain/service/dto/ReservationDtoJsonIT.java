//: com.falconcamp.cloud.rms.domain.service.dto.ReservationDtoJsonIT.java


package com.falconcamp.cloud.rms.domain.service.dto;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@JsonTest
@DisplayName("Json Mapping Test for ReservationDto - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationDtoJsonIT {

    @Autowired
    private JacksonTester<ReservationDto> jsonTester;

    private ReservationDto reservationDto;

    private UUID id;
    private int version;

    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;

    private String fullName;
    private String email;

    private OffsetDateTime startDateTime;
    private OffsetDateTime arrivalDateTime;
    private OffsetDateTime depatureDateTime;

    private int days;

    @BeforeEach
    void setUp() {

        this.id = UUID.fromString("25c69d46-e084-4fdd-8c1f-9dcb582bc991");

        this.version = 7;

        this.createdDate = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.SEPTEMBER, 22,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);
        this.lastModifiedDate = this.createdDate;

        this.fullName = "Jon Snow";
        this.email = "john.snow@winterfell.com";

        this.startDateTime = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.DECEMBER, 22,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);

        this.arrivalDateTime = this.startDateTime.minusDays(1);
        this.depatureDateTime = this.startDateTime.plusDays(MAX_RESERV_DAYS);
        this.days = 2;

        this.reservationDto = ReservationDto.builder()
                .id(this.id)
                .version((long)this.version)
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
    void test_Given_Reservation_DTO_When_Writing_To_Json_Then_Verifying_Json_Body() throws IOException {

        // Given
        Path resourceDirectory = Paths.get("src", "test",
                "resources", "ReservationDto.json");
        File jsonFile = resourceDirectory.toFile();

        // When
        String reservationDtoJson = this.jsonTester.write(this.reservationDto)
                .getJson();

        // Then
        assertThat(this.jsonTester.write(this.reservationDto))
                .isEqualToJson(jsonFile);
    }

    @Test
    void test_Reservation_Dto_When_Writing_Json_Then_Verifying_Fields() throws IOException {

        // When & Then

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathStringValue("@.id")
                .isEqualTo(this.id.toString());

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathNumberValue("@.version")
                .isEqualTo(this.version);

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathStringValue("@.startDateTime")
                .isEqualTo("2020-12-22T00:00:00-0400");

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathStringValue("@.fullName")
                .isEqualTo(this.fullName);

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathStringValue("@.email")
                .isEqualTo("john.snow@winterfell.com");

        assertThat(this.jsonTester.write(this.reservationDto))
                .extractingJsonPathNumberValue("@.days")
                .isEqualTo(this.days);
    }

}///:~