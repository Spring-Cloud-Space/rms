//: com.falconcamp.cloud.rms.bootstrap.RmsBootstrap.java


package com.falconcamp.cloud.rms.bootstrap;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;

import static com.falconcamp.cloud.rms.domain.service.ReservationService.MAX_RESERV_DAYS;
import static com.falconcamp.cloud.rms.domain.service.mappers.DateTimeMapper.DEFAULT_ZONE_OFFSET;


@Slf4j
@Component
@RequiredArgsConstructor
public class RmsBootstrap implements CommandLineRunner {

    private final IReservationRepository reservationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (this.reservationRepository.count() == 0) {
            this.loadExistingReservations();
        }
        log.debug(">>>>>>> {} reservations were loaded.",
                this.reservationRepository.count());
    }

    private void loadExistingReservations() {

        OffsetDateTime startDateTime = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.SEPTEMBER, 5,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);

        Reservation reserv1 = Reservation.builder()
                .email("yul@tecsys.com")
                .fullName("Yu LI")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS)
                .detatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS))
                .build();
        this.reservationRepository.save(reserv1);

        startDateTime = startDateTime.plusDays(10);
        Reservation reserv2 = Reservation.builder()
                .email("stevej@apple.com")
                .fullName("Steve Jobs")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS - 1)
                .detatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS - 1))
                .build();

        this.reservationRepository.save(reserv2);
    }

}///:~