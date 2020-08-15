//: com.falconcamp.cloud.rms.domain.service.IReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public interface IReservationService {

    ReservationDto save(ReservationDto reservationDtto);

    UUID cancelById(UUID id);

    List<ReservationDto> findAllReservations();

    List<ICampDay> findAvailabilitiesBetween(OffsetDateTime from, OffsetDateTime to);

}///:~