//: com.falconcamp.cloud.rms.domain.service.ReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapper;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;


@Slf4j
@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {

    private final IReservationMapper mapper;
    private final IReservationRepository reservationRepository;

    @Override
    public ReservationDto save(@NonNull Reservation reservation) {

        Reservation savedReservation = this.reservationRepository.save(
                Objects.requireNonNull(reservation));
        return this.mapper.reservationToReservationDto(savedReservation);
    }

    @Override
    public List<ReservationDto> findAllReservations() {

        return this.reservationRepository.findAll().stream()
                .map(this.mapper::reservationToReservationDto)
                .collect(ImmutableList.toImmutableList());
    }

    @Override
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