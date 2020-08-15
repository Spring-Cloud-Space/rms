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

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> findAllReservations() {

        return this.reservationRepository.findAll().stream()
                .map(this.mapper::reservationToReservationDto)
                .sorted()
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ICampDay> findAvailabilitiesBetween(
            OffsetDateTime from, OffsetDateTime to) {

        OffsetDateTime searchFromDay = ICampDay.asSearchFromDay(from);

        final List<OffsetDateTime> reservedDays = this.getAllReservedDays(
                searchFromDay, to);

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

        Result result = this.temporalValidator.validate(reservationDto);

        if (!result.isValid()) {
            throw new TemporalException(result.getResultInfo());
        }

        // Check if available
        final ReservationDto dto = Objects.requireNonNull(reservationDto).normalize();

        List<OffsetDateTime> bookDays = dto.getCampDates();

        OffsetDateTime searchFrom = ICampDay.asSearchFromDay(bookDays.get(0));
        OffsetDateTime searchTo = bookDays.get(bookDays.size() - 1).plusDays(1);

        List<OffsetDateTime> reservedDays = this.getAllReservedDays(
                searchFrom, searchTo);
        List<OffsetDateTime> unavailableDays = bookDays.stream()
                .filter(reservedDays::contains)
                .collect(ImmutableList.toImmutableList());

        if (unavailableDays.size() > 0) {
            throw CampDayUnavailableException.of(unavailableDays);
        }

        // Place new reservation
        Reservation reservation = this.mapper.reservationDtoToReservation(dto);
        Reservation savedReservation = this.reservationRepository.save(
                Objects.requireNonNull(reservation));
        return this.mapper.reservationToReservationDto(savedReservation);
    }

    @Override
    @Transactional
    public UUID cancelById(UUID id) {
        if (!this.reservationRepository.existsById(id)) {
            throw ReservationNotFoundException.of(id);
        }
        this.reservationRepository.deleteById(Objects.requireNonNull(id));
        return id;
    }

    private List<OffsetDateTime> getAllReservedDays(
            OffsetDateTime from, OffsetDateTime to) {

        return this.reservationRepository
                .findAllByStartDateTimeBetween(from, to).stream()
                .flatMap(this::getReservedDays)
                .collect(ImmutableList.toImmutableList());
    }

    private Stream<OffsetDateTime> getReservedDays(
            @NonNull Reservation reservation) {

        OffsetDateTime startDateTime = reservation.getStartDateTime();
        return IntStream.range(0, reservation.getDays())
                .mapToObj(i -> startDateTime.plusDays(i));
    }

}///:~