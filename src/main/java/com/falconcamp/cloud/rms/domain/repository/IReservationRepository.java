//: com.falconcamp.cloud.rms.domain.repository.IReservationRepository.java


package com.falconcamp.cloud.rms.domain.repository;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public interface IReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findAllByStartDateTimeBetween(
            OffsetDateTime startDateTime, OffsetDateTime endDateTime);

}///:~