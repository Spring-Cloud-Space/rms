//: com.falconcamp.cloud.rms.domain.service.IReservedCampDaysService.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;

import java.time.OffsetDateTime;
import java.util.List;


public interface IReservedCampDaysService {

    List<OffsetDateTime> getAllReservedCampDays(
            IReservationRepository repository,
            OffsetDateTime from, OffsetDateTime to);

}///:~