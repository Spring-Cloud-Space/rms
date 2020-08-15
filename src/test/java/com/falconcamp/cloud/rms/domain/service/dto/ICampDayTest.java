//: com.falconcamp.cloud.rms.domain.service.dto.ICampDayTest.java


package com.falconcamp.cloud.rms.domain.service.dto;


import com.falconcamp.cloud.rms.domain.service.IllegalSearchArgumentsException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@DisplayName("Camp Date Calculation Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ICampDayTest {

    private int year;
    private int monthValue;
    private int dayOfMonth;

    private OffsetDateTime anyDateTime;

    @BeforeEach
    void setUp() {

        this.year = 2020;
        this.monthValue = 8;
        this.dayOfMonth = 1;

        this.anyDateTime = OffsetDateTime.of(
                this.year, this.monthValue, this.dayOfMonth,
                11, 59, 59, 123,
                DEFAULT_ZONE_OFFSET);
    }

    private void assert_Being_Camp_Start_Date(@NonNull OffsetDateTime day) {
        assertThat(day.getHour()).isZero();
        assertThat(day.getMinute()).isZero();
        assertThat(day.getSecond()).isZero();
        assertThat(day.getNano()).isZero();
        assertThat(day.getOffset()).isSameAs(DEFAULT_ZONE_OFFSET);
    }

    @Test
    void test_All_Camp_Start_Date_Should_Be_12_Middlenight() {

        // Given

        // When
        OffsetDateTime newStartDay = ICampDay.normalize(anyDateTime);

        // Then
        assertThat(newStartDay.getYear()).isEqualTo(this.year);
        assertThat(newStartDay.getMonthValue()).isEqualTo(this.monthValue);
        assertThat(newStartDay.getDayOfMonth()).isEqualTo(this.dayOfMonth);
        assert_Being_Camp_Start_Date(newStartDay);
    }

    @Test
    void test_Always_Search_From_The_Next_Day() {

        // Given
        OffsetDateTime today = OffsetDateTime.now();

        // When
        OffsetDateTime from = ICampDay.fromTomorrow();

        // Then
        this.assert_Being_Camp_Start_Date(from);

        assertThat(from.getYear()).isEqualTo(today.getYear());
        assertThat(from.getMonthValue()).isEqualTo(today.getMonthValue());
        assertThat(from.getDayOfMonth()).isEqualTo(today.getDayOfMonth() + 1);
    }

    @Test
    void test_The_Actual_Search_From_Day_Should_Be_Two_Days_Before_The_Given_From_Day() {

        // Given
        final OffsetDateTime givenFromDay = this.anyDateTime;

        // When
        OffsetDateTime actualSearchFromDay = ICampDay.asSearchFromDay(givenFromDay);

        // Then
        this.assert_Being_Camp_Start_Date(actualSearchFromDay);

        assertThat(actualSearchFromDay.getDayOfMonth()).isEqualTo(
                givenFromDay.minusDays(MAX_RESERV_DAYS).getDayOfMonth());
        assertThat(actualSearchFromDay.getMonthValue()).isEqualTo(
                givenFromDay.minusDays(MAX_RESERV_DAYS).getMonthValue());
        assertThat(actualSearchFromDay.getYear()).isEqualTo(
                givenFromDay.minusDays(MAX_RESERV_DAYS).getYear());
    }

    @Test
    void test_The_Default_Months_For_Search_Is_One_Month() {

        // Given
        OffsetDateTime fromDay = ICampDay.normalize(this.anyDateTime);

        // When
        OffsetDateTime searchEndDay = ICampDay.getSearchEndDay(fromDay, 0);
        long days = DAYS.between(fromDay, searchEndDay);

        log.debug(">>>>>>> Form: {}", this.anyDateTime);
        log.debug(">>>>>>> To: {}", searchEndDay);

        // Then
        assertThat(days).isEqualTo(31L);
    }

    @Test
    void test_End_Search_Day_In_Months() {

        // Given
        OffsetDateTime fromDay = ICampDay.normalize(this.anyDateTime);

        // When
        OffsetDateTime searchEndDay = ICampDay.getSearchEndDay(fromDay, 2);
        long days = DAYS.between(fromDay, searchEndDay);

        log.debug(">>>>>>> Form: {}", this.anyDateTime);
        log.debug(">>>>>>> To: {}", searchEndDay);

        // Then
        assertThat(days).isEqualTo(61L);
    }

    @Test
    void test_The_Search_From_Day_Is_Tomorrow_By_Default() {

        // Given
        OffsetDateTime expectedFromDay = ICampDay.normalize(
                OffsetDateTime.now().plusDays(1));

        // When
        OffsetDateTime actualFromDay = ICampDay.asFromDay(null);

        // Then
        assertThat(actualFromDay).isEqualTo(expectedFromDay);
    }

    @Test
    void test_Given_LocalDate_As_From_Day_Parameter_Then_Having_Search_From_Day() {

        // Given
        LocalDate fromArg = LocalDate.now();

        // When
        OffsetDateTime actualFromDay = ICampDay.asFromDay(fromArg);

        // Then
        this.assert_Being_Camp_Start_Date(actualFromDay);

        assertThat(actualFromDay.getYear()).isEqualTo(fromArg.getYear());
        assertThat(actualFromDay.getMonthValue()).isEqualTo(fromArg.getMonthValue());
        assertThat(actualFromDay.getDayOfMonth()).isEqualTo(fromArg.getDayOfMonth());
    }

    @Test
    void test_Search_One_Month_If_End_Day_Param_Being_Absent() {

        // Given
        OffsetDateTime from = ICampDay.normalize(this.anyDateTime);
        OffsetDateTime expectedEndDay = from.plusMonths(DEFAULT_SEARCH_MONTHS);

        // When
        OffsetDateTime actualEndDay = ICampDay.calcEndDay(from, null);

        // Then
        assertThat(actualEndDay).isEqualTo(expectedEndDay);
    }

    @Test
    void test_Able_To_Calculate_End_Day_For_Search_From_Param() {

        // Given
        int days = 15;
        OffsetDateTime from = ICampDay.normalize(this.anyDateTime);
        LocalDate endDayParam = LocalDate.of(this.year,
                this.monthValue + 1, this.dayOfMonth + days);
        OffsetDateTime expectedEndDay = OffsetDateTime.of(this.year,
                this.monthValue + 1, this.dayOfMonth + days,
                0, 0, 0, 0, DEFAULT_ZONE_OFFSET);

        // When
        OffsetDateTime actualEndDay = ICampDay.calcEndDay(from, endDayParam);

        // Then
        assertThat(actualEndDay).isEqualTo(expectedEndDay);
    }

    @Test
    void test_End_Day_Should_Not_Be_Early_Than_From_Day_For_Search() {

        // Given
        int days = 15;
        OffsetDateTime from = ICampDay.normalize(this.anyDateTime);
        LocalDate endDayParam = LocalDate.of(this.year,
                this.monthValue - 1, this.dayOfMonth);

        // When
        assertThatThrownBy(() -> {
            ICampDay.calcEndDay(from, endDayParam);
        }).isInstanceOf(IllegalSearchArgumentsException.class)
                .hasMessageContaining(ILLEGAL_SEARCH_BOUNDRY_ERROR_MESSAGE);
    }

}///:~