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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private final IReservationMapper mapper;
    private final IReservationValidator temporalValidator;
    private final IReservationRepository reservationRepository;
    private final IReservedCampDaysService reservedCampDaysService;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> findAllReservations() {

        List<Reservation> allReservations = List.of();

        this.lock.readLock().lock();
        try {
            allReservations = this.reservationRepository.findAll();
        } finally {
            this.lock.readLock().unlock();
        }

        return allReservations.stream()
                .map(this.mapper::reservationToReservationDto)
                .sorted()
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ICampDay> findAvailabilitiesBetween(
            OffsetDateTime from, OffsetDateTime to) {

        OffsetDateTime searchFromDay = ICampDay.asSearchFromDay(from);
        List<OffsetDateTime> reservedDays;

        this.lock.readLock().lock();

        try {
            reservedDays = this.reservedCampDaysService.getAllReservedCampDays(
                    this.reservationRepository, searchFromDay, to);
        } finally {
            this.lock.readLock().unlock();
        }

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

        final ReservationDto newDto = Objects.requireNonNull(reservationDto)
                .normalize();

        this.lock.writeLock().lock();

        try {
            this.checkNewCampDaysAvailability(newDto);
            Reservation reservation = this.mapper.reservationDtoToReservation(newDto);
            Reservation savedReservation = reservationRepository.saveAndFlush(
                    Objects.requireNonNull(reservation));
            return this.mapper.reservationToReservationDto(savedReservation);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    @Transactional
    public UUID cancelById(UUID id) {

        this.lock.writeLock().lock();

        try {
            if (!reservationRepository.existsById(id)) {
                throw ReservationNotFoundException.of(id);
            }
            reservationRepository.deleteById(Objects.requireNonNull(id));
            return id;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    @Transactional
    public ReservationDto updateReservation(
            @NonNull UUID id, @NonNull ReservationDto reservationDto) {

        Reservation reservation = null;

        this.lock.readLock().lock();

        try {
            reservation = this.findReservationById(id);
        } finally {
            this.lock.readLock().unlock();
        }

        ReservationDto updateDto = reservationDto.normalize();

        this.updateCustomerData(reservation, updateDto);

        if (this.isNoCampDayMove(reservation, updateDto)) {
            reservation.setDays(updateDto.getDays());
            return this.mapper.reservationToReservationDto(reservation);
        }

        this.validateCampDayUpdate(updateDto);

        this.lock.writeLock().lock();

        try {
            this.checkUpdateCampDaysAvailability(reservation, updateDto);
            this.updateCampDays(reservation, updateDto);
            return this.mapper.reservationToReservationDto(reservation);
        } finally {
            this.lock.writeLock().unlock();
        }
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

}///:~