//: com.falconcamp.cloud.rms.domain.service.IReservationValidator.java


package com.falconcamp.cloud.rms.domain.service;


import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;

import com.falconcamp.cloud.rms.domain.service.ReservationTemporalValidator.Result;


public interface IReservationValidator {

    Result validate(final ReservationDto dto);

}///:~