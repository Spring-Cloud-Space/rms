//: com.falconcamp.cloud.rms.domain.service.ReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidator.Result;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapper;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;


@Slf4j
@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {

    private final IReservationMapper mapper;
    private final IReservationValidator temporalValidator;
    private final IReservationRepository reservationRepository;
    private final IReservedCampDaysService reservedCampDaysService;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> findAllReservations() {

        return reservationRepository.findAll().stream()
                .map(this.mapper::reservationToReservationDto)
                .sorted()
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ICampDay> findAvailabilitiesBetween(
            OffsetDateTime from, OffsetDateTime to) {

        OffsetDateTime searchFromDay = ICampDay.asSearchFromDay(from);

        final List<OffsetDateTime> reservedDays =
                this.reservedCampDaysService.getAllReservedCampDays(
                        this.reservationRepository, searchFromDay, to);
                // this.getAllReservedDays(searchFromDay, to);

        long allDays = DAYS.between(from, to);

        return LongStream.range(0, allDays)
                .mapToObj(i -> from.plusDays(i))
                .filter(day -> !reservedDays.contains(day))
                .map(ICampDay::ofAvailable)
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    @Transactional
    public ReservationDto save(ReservationDto reservationDto) {

        this.validateCampDayUpdate(reservationDto);

        final ReservationDto newDto = Objects.requireNonNull(reservationDto).normalize();

        this.checkNewCampDaysAvailability(newDto);

        // Place new reservation
        Reservation reservation = this.mapper.reservationDtoToReservation(newDto);
        Reservation savedReservation = reservationRepository.save(
                Objects.requireNonNull(reservation));

        return this.mapper.reservationToReservationDto(savedReservation);
    }

    @Override
    @Transactional
    public UUID cancelById(UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw ReservationNotFoundException.of(id);
        }
        reservationRepository.deleteById(Objects.requireNonNull(id));
        return id;
    }

    @Override
    @Transactional
    public ReservationDto updateReservation(
            @NonNull UUID id, @NonNull ReservationDto reservationDto) {

        final Reservation reservation = this.findReservationById(id);
        final ReservationDto updateDto = reservationDto.normalize();

        this.updateCustomerData(reservation, updateDto);

        if (this.isNoCampDayMove(reservation, updateDto)) {
            reservation.setDays(updateDto.getDays());
            return this.mapper.reservationToReservationDto(reservation);
        }

        this.validateCampDayUpdate(updateDto);

        this.checkUpdateCampDaysAvailability(reservation, updateDto);
        this.updateCampDays(reservation, updateDto);

        return this.mapper.reservationToReservationDto(reservation);
    }

    private void updateCustomerData(@NonNull Reservation reservation,
                                    @NonNull ReservationDto updateDto) {

        reservation.setFullName(updateDto.getFullName());
        reservation.setEmail(updateDto.getEmail());
        reservation.setArrivalDateTime(updateDto.getArrivalDateTime());
        reservation.setDepatureDateTime(updateDto.getDepatureDateTime());
    }

    private boolean isNoCampDayMove(@NonNull Reservation reservation,
                                    @NonNull ReservationDto updateDto) {

        return reservation.getStartDateTime().equals(updateDto.getStartDateTime()) &&
                reservation.getDays() >= updateDto.getDays();
    }

    private void validateCampDayUpdate(@NonNull ReservationDto updateDto) {
        Result result = this.temporalValidator.validate(updateDto);
        if (!result.isValid()) {
            throw new TemporalException(result.getResultInfo());
        }
    }

    private void checkNewCampDaysAvailability(@NonNull ReservationDto newDto) {

        List<OffsetDateTime> bookDays = ICampDay.getBookedDays(newDto);

        OffsetDateTime searchFrom = ICampDay.asSearchFromDay(bookDays.get(0));
        OffsetDateTime searchTo = bookDays.get(bookDays.size() - 1).plusDays(1);

        List<OffsetDateTime> reservedDays =
                this.reservedCampDaysService.getAllReservedCampDays(
                        this.reservationRepository, searchFrom, searchTo);

                // this.getAllReservedDays(searchFrom, searchTo);

        List<OffsetDateTime> unavailableDays = bookDays.stream()
                .filter(reservedDays::contains)
                .collect(ImmutableList.toImmutableList());

        if (unavailableDays.size() > 0) {
            throw CampDayUnavailableException.of(unavailableDays);
        }
    }

    private void checkUpdateCampDaysAvailability(
            @NonNull Reservation reservation, @NonNull ReservationDto updateDto) {

        List<OffsetDateTime> bookDays = ICampDay.getBookedDays(updateDto);
        List<OffsetDateTime> bookedDays = ICampDay.getBookedDays(reservation);

        OffsetDateTime searchFrom = ICampDay.asSearchFromDay(bookDays.get(0));
        OffsetDateTime searchTo = bookDays.get(bookDays.size() - 1).plusDays(1);

        List<OffsetDateTime> reservedDays =
                this.reservedCampDaysService.getAllReservedCampDays(
                        this.reservationRepository, searchFrom, searchTo)
                                .stream()
                                .filter(day -> !bookedDays.contains(day))
                                .collect(ImmutableList.toImmutableList());

        List<OffsetDateTime> unavailableDays = bookDays.stream()
                .filter(reservedDays::contains)
                .collect(ImmutableList.toImmutableList());

        if (unavailableDays.size() > 0) {
            throw CampDayUnavailableException.of(unavailableDays);
        }
    }

    private void updateCampDays(@NonNull Reservation reservation,
                                @NonNull ReservationDto updateDto) {

        reservation.setDays(updateDto.getDays());
        reservation.setStartDateTime(updateDto.getStartDateTime());
    }

    private Reservation findReservationById(@NonNull UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ReservationNotFoundException.of(id));
    }

//    private List<OffsetDateTime> getAllReservedDays(
//            OffsetDateTime from, OffsetDateTime to) {
//
//        return reservationRepository
//                .findAllByStartDateTimeBetween(from, to).stream()
//                .flatMap(this::getReservedDays)
//                .collect(ImmutableList.toImmutableList());
//    }
//
//    private Stream<OffsetDateTime> getReservedDays(
//            @NonNull Reservation reservation) {
//
//        OffsetDateTime startDateTime = reservation.getStartDateTime();
//        return IntStream.range(0, reservation.getDays())
//                .mapToObj(i -> startDateTime.plusDays(i));
//    }

}///:~