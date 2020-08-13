//: com.falconcamp.cloud.rms.domain.service.IReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;

import java.util.List;


public interface IReservationService {

    ReservationDto save(Reservation reservation);

    List<ReservationDto> findAllReservations();

    List<ICampDay> findAvailabilities();

}///:~