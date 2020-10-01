//: com.falconcamp.cloud.rms.web.controller.RmsClientIT.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.github.jenspiegsa.wiremockextension.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@ExtendWith(WireMockExtension.class)
@WireMockSettings(failOnUnmatchedRequests = true)
@DisplayName("Test Rms Client with WireMock - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RmsClientIT {

    private static final String URL_TEMPLATE = "%s:%d%s";
    private static final String HOST = "http://localhost";
    private static final String RESERVATION_PATH = "/api/v1/resv";

    @InjectServer
    private WireMockServer wireMockServer;

    @ConfigureWireMock
    Options options = wireMockConfig()
            .dynamicPort()
            .notifier(new ConsoleNotifier(true));

    private TestRestTemplate restTemplate;

    private String reservationUrlString;
    private ReservationDto dto_1;
    private RmsClient rmsClient;


    @BeforeEach
    void setUp() {

        OffsetDateTime startDay = OffsetDateTime.of(
                2020, 10, 29,
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

        int port = this.wireMockServer.port();

        this.reservationUrlString = String.format(
                URL_TEMPLATE, HOST, port, RESERVATION_PATH);

        this.restTemplate = new TestRestTemplate();

        this.rmsClient = RmsClient.of(this.reservationUrlString,
                this.restTemplate, this.dto_1);
    }

    @Test
    void test_Sending_Request_To_Update_Reservation() {

        // Given
        this.wireMockServer.stubFor(put(RESERVATION_PATH).willReturn(noContent()));

        // When
        ResponseEntity<String> responseEntity = this.rmsClient.updateReservation();

        // Then
        verify(1, putRequestedFor(urlEqualTo(RESERVATION_PATH)));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}///:~