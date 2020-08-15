//: com.falconcamp.cloud.rms.web.controller.ReservationExceptionHandler.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;


@Slf4j
@ControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(IllegalSearchArgumentsException.class)
    protected ResponseEntity<String> handleInvalidRequestParameters(
            IllegalSearchArgumentsException exception) {

        String errMsg = String.format("%s From %s to %s.", exception.getMessage(),
                exception.getFrom(), exception.getTo());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    protected ResponseEntity<String> handleInvalidRequestParameters(
            ConversionFailedException exception) {

        String errMsg = String.format("The request parameter is invalid: '%s'",
                exception.getValue());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> methodArgumaneValidationErrorHandler(
            MethodArgumentNotValidException e) {

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errors = allErrors.stream()
                .map(oe -> String.join(" ", ((FieldError) oe).getField(),
                        oe.getDefaultMessage()))
                .collect(toUnmodifiableList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> modelValidationErrorHandler(
            ConstraintViolationException cve) {

        Set<ConstraintViolation<?>> allErrors = cve.getConstraintViolations();

        List<String> errMsg = allErrors.stream()
                .map(cv -> String.join(" ",
                        cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(toList());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List> handleDateTimeParseException(
            HttpMessageNotReadableException exception) {

        String errMsg = null;
        Throwable cause = exception.getCause();

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException) cause;
            String inputValue = formatException.getValue().toString();
            String fieldName = formatException.getPath().get(0).getFieldName();
            errMsg = String.format(
                    "The format of input value '%s' is invalid for '%s'",
                    inputValue, fieldName);
        } else {
            errMsg = cause.getMessage();
        }

        return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CampDayUnavailableException.class)
    public ResponseEntity<String> handleCampDayUnavailableException(
            CampDayUnavailableException ex) {

        return new ResponseEntity(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<String> handleReservationNotFoundException(
            ReservationNotFoundException ex) {

        return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TemporalException.class)
    public ResponseEntity<String> handleTemporalException(TemporalException ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List> handleBindException(BindException ex) {
        return new ResponseEntity(ex.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

}///:~