//: com.falconcamp.cloud.rms.domain.service.ReservedCampDaysService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Component
final class ReservedCampDaysService implements IReservedCampDaysService {

    @Override
    public List<OffsetDateTime> getAllReservedCampDays(
            @NonNull IReservationRepository repository,
            @NonNull OffsetDateTime from, @NonNull OffsetDateTime to) {

        return repository
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