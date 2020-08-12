//: com.falconcamp.cloud.rms.domain.service.ReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapper;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {

    public static final int MIN_RESERV_DAYS = 1;
    public static final int MAX_RESERV_DAYS = 3;

    private final IReservationMapper mapper;
    private final IReservationRepository reservationRepository;

    @Override
    public ReservationDto save(Reservation reservation) {

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

}///:~