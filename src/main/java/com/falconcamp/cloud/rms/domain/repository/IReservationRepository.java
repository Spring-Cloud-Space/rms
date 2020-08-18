//: com.falconcamp.cloud.rms.domain.repository.IReservationRepository.java


package com.falconcamp.cloud.rms.domain.repository;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IReservationRepository extends JpaRepository<Reservation, UUID> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Reservation> findById(UUID id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    List<Reservation> findAllByStartDateTimeBetween(
            OffsetDateTime startDateTime, OffsetDateTime endDateTime);

}///:~