//: com.falconcamp.cloud.rms.web.controller.ReservationController.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.IReservationService;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/resv")
@AllArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<ReservationDto>> listReservations() {
        List<ReservationDto> resvList = this.reservationService.findAllReservations();
        return new ResponseEntity<>(resvList, HttpStatus.OK);
    }

}///:~