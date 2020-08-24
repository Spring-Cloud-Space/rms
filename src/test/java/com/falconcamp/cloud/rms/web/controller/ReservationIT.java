//: com.falconcamp.cloud.rms.web.controller.ReservationIT.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.config.PropertiesConfiguration.RmsProperties;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("The Integration Test of Reservation Endpoints - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationIT {

    private static final String URL_TEMPLATE = "%s:%d%s";

    private static final String RESERVATION_PATH = "/api/v1/resv";

    @LocalServerPort
    private int port;

    @Autowired
    private RmsProperties rmsProperties;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String reservationUrlString;

    private OffsetDateTime startDay;
    private ReservationDto dto_1;
    private ReservationDto dto_2;

    private ExecutorService exitingExecutorService;

    @BeforeEach
    void setUp() {

        this.reservationUrlString = String.format(URL_TEMPLATE,
                rmsProperties.getRmsServiceHost(), this.port, RESERVATION_PATH);

        this.startDay = OffsetDateTime.of(
                2020, 9, 2,
                0, 0, 0, 0,
                DEFAULT_ZONE_OFFSET);

        this.dto_1 = ReservationDto.builder()
                .fullName("Arya Stark")
                .email("arya.stark@black_and_white_house.com")
                .startDateTime(startDay)
                .arrivalDateTime(startDay.minusDays(1))
                .depatureDateTime(startDay.plusDays(MAX_RESERV_DAYS))
                .days(MAX_RESERV_DAYS)
                .build();

        this.dto_2 = ReservationDto.builder()
                .fullName("Sansa Stark")
                .email("sansa.stark@winterfell.com")
                .startDateTime(startDay.plusDays(1))
                .arrivalDateTime(startDay)
                .depatureDateTime(startDay.plusDays(MAX_RESERV_DAYS))
                .days(MAX_RESERV_DAYS - 1)
                .build();

        this.exitingExecutorService = this.buildExecutorService();
    }

    @Order(1)
    @RepeatedTest(value = 9, name = RepeatedTest.LONG_DISPLAY_NAME)
    void test_Able_To_Handle_Concurrent_Requests_To_Reserve_The_Campsite(
            RepetitionInfo repetitionInfo) {

        // Given
        long expectedSuccessfulReservationCount =
                (repetitionInfo.getCurrentRepetition() == 1) ? 1 : 0;

        List<RmsClient> clients = List.of(
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_1),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_1),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_1),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_1),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_2),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_2),
                RmsClient.of(this.reservationUrlString,
                        new TestRestTemplate(), this.dto_2)
        );

        List<CompletableFuture<ResponseEntity<String>>> futures = clients.stream()
                .map(this::placeNewReservation)
                .collect(ImmutableList.toImmutableList());

        // When
        long successfulReservationCount = futures.stream()
                .map(CompletableFuture::join)
                .map(ResponseEntity::getStatusCode)
                .filter(HttpStatus::is2xxSuccessful)
                .count();

        // Then
        assertThat(successfulReservationCount).isEqualTo(
                expectedSuccessfulReservationCount);
    }

    @Order(2)
    @RepeatedTest(value = 9, name = RepeatedTest.LONG_DISPLAY_NAME)
    void test_Able_To_Update_An_Excisting_Reservation() {

        // Given
        ResponseEntity<List> entity = this.testRestTemplate
                .getForEntity(this.reservationUrlString, List.class);
        List<Map> all = entity.getBody();
        String id = (String)all.get(1).get("id");
        String startDate = (String)all.get(1).get("startDateTime");

        String updateUrl = String.join("/",
                this.reservationUrlString, id);
        OffsetDateTime secondUpdatedStartDay = startDay.plusDays(6);
        ReservationDto dto_3 = ReservationDto.builder()
                .fullName("Arya Stark")
                .email("arya.stark@black_and_white_house.com")
                .startDateTime(secondUpdatedStartDay)
                .arrivalDateTime(secondUpdatedStartDay.minusDays(1))
                .depatureDateTime(secondUpdatedStartDay.plusDays(MAX_RESERV_DAYS))
                .days(MAX_RESERV_DAYS)
                .build();

        List<RmsClient> clients = List.of(
                RmsClient.of(updateUrl, new TestRestTemplate(), this.dto_1),
                RmsClient.of(updateUrl, new TestRestTemplate(), dto_3));

        List<CompletableFuture<ResponseEntity<String>>> futures = clients.stream()
                .map(this::updateReservation)
                .collect(ImmutableList.toImmutableList());

        // When
        long successfulReservationCount = futures.stream()
                .map(CompletableFuture::join)
                .map(ResponseEntity::getStatusCode)
                .filter(HttpStatus::is2xxSuccessful)
                .count();

        // Then
        assertThat(successfulReservationCount).isOne();
    }

    private CompletableFuture<ResponseEntity<String>> placeNewReservation(
            @NonNull RmsClient client) {

        return CompletableFuture.supplyAsync(client::placeNewReservation,
                this.exitingExecutorService);
    }

    private CompletableFuture<ResponseEntity<String>> updateReservation(
            @NonNull RmsClient client) {

        return CompletableFuture.supplyAsync(client::updateReservation,
                this.exitingExecutorService);
    }

    private ExecutorService buildExecutorService() {
        BlockingQueue<Runnable> workingQueue = new LinkedTransferQueue<>();

        ThreadPoolExecutor executor =  new ThreadPoolExecutor(
                10, 10, 1000,
                TimeUnit.MILLISECONDS, workingQueue);
        executor.allowCoreThreadTimeOut(true);

        return MoreExecutors.getExitingExecutorService(executor,
                Duration.ofMillis(1000));
    }

}///:~