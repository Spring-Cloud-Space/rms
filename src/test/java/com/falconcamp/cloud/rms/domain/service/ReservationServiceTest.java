//: com.falconcamp.cloud.rms.domain.service.ReservationServiceTest.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapper;
import com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidator.Result;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Reservation Service Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationServiceTest {

    @Mock
    private IReservationRepository repository;

    @Mock
    private IReservationMapper mapper;

    @Mock
    private IReservationValidator validator;

    @Mock
    private IReservedCampDaysService reservedCampDaysService;

    private ReservationService service;

    @Mock
    private Reservation reservation;

    @Mock
    private ReservationDto updateDto;

    @Mock
    private ReservationDto updatedDto;

    @Mock
    private ReservationDto rservationDto;

    private UUID id;

    private String fullName;
    private String email;
    private OffsetDateTime arrivalDateTime;
    private OffsetDateTime depatureDateTime;

    private OffsetDateTime today;

    private int advanceDays;

    @BeforeEach
    void setUp() {

        this.service = new ReservationService(this.mapper, this.validator,
                this.repository, this.reservedCampDaysService);

        this.advanceDays = 15;

        this.today = ICampDay.normalize(OffsetDateTime.now());

        this.id = UUID.randomUUID();
        this.fullName = "Jon Snow";
        this.email = "jon.snow@winterfell.com";
        this.arrivalDateTime = today.plusDays(this.advanceDays);
        this.depatureDateTime = today.plusDays(this.advanceDays + MAX_RESERV_DAYS);
    }

    @Nested
    class ReservationUpdateTest {

        private void prepareArgumentsForUpdate() {
            Optional<Reservation> resevOpt = Optional.of(reservation);
            given(repository.findById(id)).willReturn(resevOpt);
            given(rservationDto.normalize()).willReturn(updateDto);
        }

        @Test
        void test_Throw_Not_Found_Exception_If_Repository_Has_No_Reservation_For_Id() {

            // Given
            given(repository.findById(id)).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.updateReservation(id, rservationDto))
                    .isInstanceOf(ReservationNotFoundException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void test_Not_Able_To_Book_Too_Early_Or_Too_Late() {

            // Given
            this.prepareArgumentsForUpdate();

            OffsetDateTime bookedStartDay = today.plusDays(advanceDays);
            given(reservation.getStartDateTime()).willReturn(bookedStartDay);
            given(updateDto.getStartDateTime()).willReturn(bookedStartDay.minusDays(7));

            Result result = mock(Result.class);
            String validateInfo = RandomStringUtils.randomAlphanumeric(30);
            given(validator.validate(updateDto)).willReturn(result);
            given(result.isValid()).willReturn(false);
            given(result.getResultInfo()).willReturn(validateInfo);

            // When & Then
            assertThatThrownBy(() -> service.updateReservation(id, rservationDto))
                    .isInstanceOf(TemporalException.class)
                    .hasMessageContaining(validateInfo);
        }

        private void prepareForUpdateValidationAndavailabilityCheck() {
            OffsetDateTime bookedStartDay = today.plusDays(advanceDays);
            given(reservation.getStartDateTime()).willReturn(bookedStartDay);
            given(updateDto.getStartDateTime()).willReturn(bookedStartDay.minusDays(7));

            Result result = mock(Result.class);
            given(validator.validate(updateDto)).willReturn(result);
            given(result.isValid()).willReturn(true);

            given(reservation.getStartDateTime()).willReturn(
                    today.plusDays(advanceDays));
            given(reservation.getDays()).willReturn(MAX_RESERV_DAYS);

            int newAdvance = advanceDays - 5;
            given(updateDto.getStartDateTime()).willReturn(
                    today.plusDays(newAdvance));
            given(updateDto.getDays()).willReturn(MAX_RESERV_DAYS);
        }

        @Test
        void test_Not_Able_To_Update_Reservation_If_No_Camp_Day_Available_For_New_Book_Info() {

            // Given
            this.prepareArgumentsForUpdate();

            this.prepareForUpdateValidationAndavailabilityCheck();

            List<OffsetDateTime> reservedDays = List.of(
                    ICampDay.normalize(OffsetDateTime.now().plusDays(12)),
                    ICampDay.normalize(OffsetDateTime.now().plusDays(13)),
                    ICampDay.normalize(OffsetDateTime.now().plusDays(14)),
                    ICampDay.normalize(OffsetDateTime.now().plusDays(15))
            );

            given(reservedCampDaysService.getAllReservedCampDays(
                    same(repository), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                    .willReturn(reservedDays);

            // When & Then
            assertThatThrownBy(() -> service.updateReservation(id, rservationDto))
                    .isInstanceOf(CampDayUnavailableException.class);
        }

        @Test
        void test_Able_To_Update_Reservation_If_Having_Camp_Days_Available() {

            // Given
            this.prepareArgumentsForUpdate();

            this.prepareForUpdateValidationAndavailabilityCheck();

            given(reservedCampDaysService.getAllReservedCampDays(
                    same(repository), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                    .willReturn(List.of());

            int days = 2;
            given(updateDto.getDays()).willReturn(days);

            given(mapper.reservationToReservationDto(reservation))
                    .willReturn(updatedDto);

            // When
            ReservationDto actualUpdatedDto = service.updateReservation(
                    id, rservationDto);

            // Then
            assertThat(actualUpdatedDto).isSameAs(updatedDto);

            then(reservation).should(times(1))
                    .setDays(days);

            then(reservation).should(times(1))
                    .setStartDateTime(any(OffsetDateTime.class));
        }

        @Test
        void test_Only_Update_Customer_Data_If_There_Is_No_Camp_Day_Move() {

            // Given
            this.prepareArgumentsForUpdate();

            given(updateDto.getFullName()).willReturn(fullName);
            given(updateDto.getEmail()).willReturn(email);
            given(updateDto.getArrivalDateTime()).willReturn(arrivalDateTime);
            given(updateDto.getDepatureDateTime()).willReturn(depatureDateTime);

            OffsetDateTime bookedStartDay = today.plusDays(advanceDays);
            given(reservation.getStartDateTime()).willReturn(bookedStartDay);
            given(updateDto.getStartDateTime()).willReturn(bookedStartDay);

            given(reservation.getDays()).willReturn(MAX_RESERV_DAYS);
            given(updateDto.getDays()).willReturn(MAX_RESERV_DAYS - 1);

            given(mapper.reservationToReservationDto(reservation))
                    .willReturn(updatedDto);

            // When
            ReservationDto actualUpdatedDto = service.updateReservation(
                    id, rservationDto);

            // Then
            then(reservation).should(times(1))
                    .setDays(MAX_RESERV_DAYS - 1);

            then(reservation).should(times(1))
                    .setFullName(fullName);
            then(reservation).should(times(1))
                    .setEmail(email);
            then(reservation).should(times(1))
                    .setArrivalDateTime(arrivalDateTime);
            then(reservation).should(times(1))
                    .setDepatureDateTime(depatureDateTime);

            assertThat(actualUpdatedDto).isSameAs(updatedDto);
        }

    }

}///:~