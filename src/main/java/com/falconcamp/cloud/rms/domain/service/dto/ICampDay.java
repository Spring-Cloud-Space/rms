//: com.falconcamp.cloud.rms.domain.service.dto.ICampDay.java


package com.falconcamp.cloud.rms.domain.service.dto;


import java.time.OffsetDateTime;


public interface ICampDay {

    OffsetDateTime getDay();
    boolean isReserved();
    boolean isAvailable();

}///:~