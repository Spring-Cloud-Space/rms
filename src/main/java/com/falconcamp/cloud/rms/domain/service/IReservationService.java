//: com.falconcamp.cloud.rms.domain.service.IReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;

import java.util.List;


public interface IReservationService {

    Reservation save(Reservation reservation);

    List<Reservation> findAllReservations();

}///:~