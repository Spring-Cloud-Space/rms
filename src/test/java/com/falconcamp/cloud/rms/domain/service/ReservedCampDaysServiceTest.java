//: com.falconcamp.cloud.rms.domain.service.ReservedCampDaysServiceTest.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Reservation Service Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservedCampDaysServiceTest {

    @Mock
    private IReservationRepository repository;

    @Mock
    private Reservation resev_1;

    @Mock
    private Reservation resev_2;

    private OffsetDateTime from;
    private OffsetDateTime to;

    private List<Reservation> reservations;

    private ReservedCampDaysService service;

    private OffsetDateTime today;

    @BeforeEach
    void setUp() {

        this.service = new ReservedCampDaysService();

        this.today = OffsetDateTime.of(2020, 9, 1,
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);

        this.from = this.today;
        this.to = this.from.plusMonths(1);

        this.reservations = List.of(this.resev_1, this.resev_2);
    }

    @Test
    void test_Given_Date_Range_When_Search_Then_Get_Reserved_Camp_Days_Back() {

        // Given
        given(this.repository.findAllByStartDateTimeBetween(this.from, this.to))
                .willReturn(this.reservations);

        given(resev_1.getDays()).willReturn(3);
        given(resev_1.getStartDateTime()).willReturn(this.today.plusDays(5));

        given(resev_2.getDays()).willReturn(2);
        given(resev_2.getStartDateTime()).willReturn(this.today.plusDays(15));

        List<OffsetDateTime> expectedReservedDays = List.of(
                OffsetDateTime.of(2020, 9, 6,
                        0, 0, 0, 0,
                        DEFAULT_ZONE_OFFSET),
                OffsetDateTime.of(2020, 9, 7,
                        0, 0, 0, 0,
                        DEFAULT_ZONE_OFFSET),
                OffsetDateTime.of(2020, 9, 8,
                        0, 0, 0, 0,
                        DEFAULT_ZONE_OFFSET),
                OffsetDateTime.of(2020, 9, 16,
                        0, 0, 0, 0,
                        DEFAULT_ZONE_OFFSET),
                OffsetDateTime.of(2020, 9, 17,
                        0, 0, 0, 0,
                        DEFAULT_ZONE_OFFSET)
        );

        // When
        List<OffsetDateTime> actualReservedDays =
                this.service.getAllReservedCampDays(this.repository, this.from,
                        this.to);

        // Then
        // System.out.println(actualReservedDays);
        assertThat(actualReservedDays).isEqualTo(expectedReservedDays);
    }

}///:~