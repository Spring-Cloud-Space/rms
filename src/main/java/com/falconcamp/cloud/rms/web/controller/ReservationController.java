//: com.falconcamp.cloud.rms.web.controller.ReservationController.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.IReservationService;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

        if (resvList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Reservation Found.");
        }

        return new ResponseEntity<>(resvList, HttpStatus.OK);
    }

    @GetMapping(path = "/avail", produces = {"application/json"})
    public ResponseEntity<List<ICampDay>> listAvailabilities(
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        OffsetDateTime fromDateTime = ICampDay.asFromDay(from);
        OffsetDateTime toDateTime = ICampDay.calcEndDay(fromDateTime, to);

        List<ICampDay> availList = this.reservationService
                .findAvailabilitiesBetween(fromDateTime, toDateTime);

        log.debug(">>>>>>> {} days are available.", availList.size());

        if (availList.isEmpty()) {
            String msg = String.format(
                    "The camp site is not available between %s to %s.", from, to);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }

        return new ResponseEntity<>(availList, HttpStatus.OK);
    }

    @PostMapping(path = "/resv")
    public ResponseEntity<String> saveNewReservation(
            @Valid @RequestBody ReservationDto reservationDto) {

        String newReservationId = this.reservationService.save(reservationDto)
                .getId()
                .toString();

        String responseMessage = String.format(
                "Your new reservation ID is '%s'", newReservationId);

        return new ResponseEntity(responseMessage, HttpStatus.CREATED);
    }

}///:~