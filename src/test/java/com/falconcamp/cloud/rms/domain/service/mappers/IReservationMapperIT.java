//: com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapperIT.java


package com.falconcamp.cloud.rms.domain.service.mappers;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.MAX_RESERV_DAYS;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@DisplayName("Reservation Mapper Test - ")
// @ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IReservationMapperIT {

    private UUID id;
    private Timestamp createDate;
    private Timestamp lastModifiedDate;
    private long version;
    private String fullName;
    private String email;
    private OffsetDateTime startDateTime;

    private LocalDateTime localDateTime;

    private Reservation reservation;
    private ReservationDto dto;

    @Autowired
    private IReservationMapper mapper;

    @Autowired
    private DateTimeMapper dateTimeMapper;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();
        this.version = RandomUtils.nextLong(1, 1000);
        this.fullName = "Steve Jobs";
        this.email = "steve.jobs@apple.com";
        this.localDateTime = LocalDateTime.of(
                2020, Month.SEPTEMBER, 5,
                0, 0, 0);
        this.startDateTime = OffsetDateTime.of(localDateTime, DEFAULT_ZONE_OFFSET);
        this.createDate = Timestamp.valueOf(this.localDateTime);
        this.lastModifiedDate = Timestamp.valueOf(this.localDateTime);
    }

    @Test
    void test_Given_Reservation_When_Mapping_Then_Get_DTO_Back() {

        // Given
        this.reservation = Reservation.builder()
                .id(this.id)
                .version(this.version)
                .createdDate(this.createDate)
                .lastModifiedDate(this.lastModifiedDate)
                .fullName(this.fullName)
                .email(this.email)
                .startDateTime(this.startDateTime)
                .arrivalDateTime(this.startDateTime.minusDays(1))
                .detatureDateTime(this.startDateTime.plusDays(MAX_RESERV_DAYS))
                .days(MAX_RESERV_DAYS)
                .build();

        // When
        this.dto = this.mapper.reservationToReservationDto(this.reservation);

        // Then
        assertThat(this.dto.getId()).isEqualTo(this.id);
        assertThat(this.dto.getVersion()).isEqualTo(this.version);
        assertThat(this.dto.getCreatedDate()).isEqualTo(
                this.dateTimeMapper.asOffsetDateTime(this.createDate));
        assertThat(this.dto.getLastModifiedDate()).isEqualTo(
                this.dateTimeMapper.asOffsetDateTime(this.lastModifiedDate));
        assertThat(this.dto.getFullName()).isEqualTo(this.fullName);
        assertThat(this.dto.getEmail()).isEqualTo(this.email);
        assertThat(this.dto.getStartDateTime()).isEqualTo(this.startDateTime);
        assertThat(this.dto.getArrivalDateTime()).isEqualTo(
                this.startDateTime.minusDays(1));
        assertThat(this.dto.getDetatureDateTime()).isEqualTo(
                this.startDateTime.plusDays(MAX_RESERV_DAYS));
        assertThat(this.dto.getDays()).isEqualTo(MAX_RESERV_DAYS);
    }

    @Test
    void test_Given_Reservation_DTO_When_Mapping_Then_Get_Reservation_Back() {

        // Given
        this.dto = ReservationDto.builder()
                .id(this.id)
                .version(this.version)
                .createdDate(this.dateTimeMapper.asOffsetDateTime(this.createDate))
                .lastModifiedDate(this.dateTimeMapper.asOffsetDateTime(this.lastModifiedDate))
                .fullName(this.fullName)
                .email(this.email)
                .startDateTime(this.startDateTime)
                .arrivalDateTime(this.startDateTime.minusDays(1))
                .detatureDateTime(this.startDateTime.plusDays(MAX_RESERV_DAYS))
                .days(MAX_RESERV_DAYS)
                .build();

        // When
        this.reservation = this.mapper.reservationDtoToReservation(this.dto);

        // Then
        assertThat(this.reservation.getId()).isEqualTo(this.id);
        assertThat(this.reservation.getVersion()).isEqualTo(this.version);
        assertThat(this.reservation.getCreatedDate()).isEqualTo(this.createDate);
        assertThat(this.reservation.getLastModifiedDate()).isEqualTo(this.lastModifiedDate);
        assertThat(this.reservation.getFullName()).isEqualTo(this.fullName);
        assertThat(this.reservation.getEmail()).isEqualTo(this.email);
        assertThat(this.reservation.getStartDateTime()).isEqualTo(this.startDateTime);
        assertThat(this.reservation.getArrivalDateTime()).isEqualTo(
                this.startDateTime.minusDays(1));
        assertThat(this.reservation.getDetatureDateTime()).isEqualTo(
                this.startDateTime.plusDays(MAX_RESERV_DAYS));
        assertThat(this.reservation.getDays()).isEqualTo(MAX_RESERV_DAYS);
    }

}///:~