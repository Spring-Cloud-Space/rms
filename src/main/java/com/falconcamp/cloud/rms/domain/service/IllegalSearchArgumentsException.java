//: com.falconcamp.cloud.rms.domain.service.IllegalSearchArgumentsException.java


package com.falconcamp.cloud.rms.domain.service;


import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;


@Getter
@ToString
public class IllegalSearchArgumentsException extends IllegalArgumentException {

    private final OffsetDateTime from;
    private final OffsetDateTime to;

    private IllegalSearchArgumentsException(
            OffsetDateTime from, OffsetDateTime to, String errMsg) {

        super(errMsg);

        this.from = from;
        this.to = to;
    }

    public static IllegalSearchArgumentsException of(
            OffsetDateTime from, OffsetDateTime to, String errMsg) {
        return new IllegalSearchArgumentsException(from, to, errMsg);
    }

}///:~