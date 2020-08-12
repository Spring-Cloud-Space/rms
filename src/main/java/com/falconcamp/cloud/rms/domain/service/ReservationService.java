//: com.falconcamp.cloud.rms.domain.service.ReservationService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {

    public static final int MIN_RESERV_DAYS = 1;
    public static final int MAX_RESERV_DAYS = 3;

    private final IReservationRepository reservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return null;
    }

    @Override
    public List<Reservation> findAllReservations() {
        return null;
    }

}///:~