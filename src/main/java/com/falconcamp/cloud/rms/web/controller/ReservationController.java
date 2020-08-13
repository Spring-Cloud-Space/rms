//: com.falconcamp.cloud.rms.web.controller.ReservationController.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.IReservationService;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping(path = "/resv", produces = {"application/json"})
    public ResponseEntity<List<ReservationDto>> listReservations() {
        List<ReservationDto> resvList = this.reservationService.findAllReservations();
        return new ResponseEntity<>(resvList, HttpStatus.OK);
    }

    @GetMapping(path = "/avail", produces = {"application/json"})
    public ResponseEntity<List<ICampDay>> listAvailabilities() {
        List<ICampDay> availList = this.reservationService.findAvailabilities();
        log.debug(">>>>>>> {} days are available.", availList.size());
        return new ResponseEntity<>(availList, HttpStatus.OK);
    }

}///:~