//: com.falconcamp.cloud.rms.domain.service.mappers.IReservationMapper.java


package com.falconcamp.cloud.rms.domain.service.mappers;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import org.mapstruct.Mapper;


@Mapper(uses = {DateTimeMapper.class})
public interface IReservationMapper {

    ReservationDto reservationToReservationDto(Reservation reservation);

    Reservation reservationDtoToReservation(ReservationDto dto);

}///:~