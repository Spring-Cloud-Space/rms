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

import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.CampDay.MAX_RESERV_DAYS;


@Slf4j
@Component
@RequiredArgsConstructor
public class RmsBootstrap implements CommandLineRunner {

    private final IReservationRepository reservationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (reservationRepository.count() == 0) {
            this.loadExistingReservations();
        }
        log.debug(">>>>>>> {} reservations were loaded.",
                reservationRepository.count());
    }

    private void loadExistingReservations() {

        // August
        OffsetDateTime startDateTime = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.SEPTEMBER, 27,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);

        Reservation reserv0 = Reservation.builder()
                .email("bill.gates@microsoft.com")
                .fullName("Bill Gates")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS - 1)
                .depatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS - 1))
                .build();
        reservationRepository.save(reserv0);

        // September
        startDateTime = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.OCTOBER, 5,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);

        Reservation reserv1 = Reservation.builder()
                .email("yul@tecsys.com")
                .fullName("Yu LI")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS)
                .depatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS))
                .build();

        reservationRepository.save(reserv1);

        startDateTime = startDateTime.plusDays(6);

        Reservation reserv2 = Reservation.builder()
                .email("john.snow@winterfell.com")
                .fullName("Jon Snow")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS - 1)
                .depatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS - 1))
                .build();

        reservationRepository.save(reserv2);

        startDateTime = startDateTime.plusDays(4);

        Reservation reserv3 = Reservation.builder()
                .email("stevej@apple.com")
                .fullName("Steve Jobs")
                .startDateTime(startDateTime)
                .arrivalDateTime(startDateTime.minusDays(1))
                .days(MAX_RESERV_DAYS - 1)
                .depatureDateTime(startDateTime.plusDays(MAX_RESERV_DAYS - 1))
                .build();

        reservationRepository.save(reserv3);
    }

}///:~