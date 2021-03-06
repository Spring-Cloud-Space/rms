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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ReservationController {

    static final String SUCCESSFULLY_PLACED_NEW_RESERVATION_MSG_TEMPLATE =
            "Your new reservation ID is '%s'";

    private final IReservationService reservationService;

    @GetMapping(path = "/resv/{id}", produces = {"application/json"})
    public ResponseEntity<ReservationDto> getReservation(
            @PathVariable("id") UUID id) {

        ReservationDto reservationDto = this.reservationService.getReservation(id);

        return new ResponseEntity<>(reservationDto, HttpStatus.OK);
    }

    @GetMapping(path = "/resv", produces = {"application/json"})
    public ResponseEntity<List<ReservationDto>> listReservations() {

        List<ReservationDto> resvList = reservationService.findAllReservations();

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
            // @RequestParam(name = "months") @Min(1) @Max(3) int months) {

        OffsetDateTime fromDateTime = ICampDay.asFromDay(from);
        OffsetDateTime toDateTime = ICampDay.calcEndDay(fromDateTime, to);

        List<ICampDay> availList = reservationService
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
    public ResponseEntity<String> placeNewReservation(
            @Valid @RequestBody ReservationDto reservationDto) {

        ReservationDto newReservationDto =
                this.reservationService.save(reservationDto);

        String newReservationId = newReservationDto.getId().toString();

        String responseMessage = String.format(
                SUCCESSFULLY_PLACED_NEW_RESERVATION_MSG_TEMPLATE,
                newReservationId);

        return new ResponseEntity(responseMessage, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/resv/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable("id") UUID id) {

        UUID cancelledId = reservationService.cancelById(id);
        String msg = String.format("Reservation '%s' was cancelled.", id);
        return new ResponseEntity(msg, HttpStatus.OK);
    }

    @PutMapping("/resv/{id}")
    public ResponseEntity updateReservationById(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ReservationDto reservationDto) {

        return new ResponseEntity(
                reservationService.updateReservation(id, reservationDto),
                HttpStatus.NO_CONTENT);
    }

}///:~