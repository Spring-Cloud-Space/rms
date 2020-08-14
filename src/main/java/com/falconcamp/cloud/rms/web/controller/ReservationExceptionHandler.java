//: com.falconcamp.cloud.rms.web.controller.ReservationExceptionHandler.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.IllegalSearchArgumentsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(IllegalSearchArgumentsException.class)
    protected ResponseEntity<String> handleConflict(
            IllegalSearchArgumentsException exception) {

        String errMsg = String.format("%s From %s to %s.", exception.getMessage(),
                exception.getFrom(), exception.getTo());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    protected ResponseEntity<String> handleConflict(
            ConversionFailedException exception) {

        String errMsg = String.format("The request parameter is invalid: '%s'",
                exception.getValue());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

}///:~