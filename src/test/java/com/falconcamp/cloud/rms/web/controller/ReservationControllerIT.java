//: com.falconcamp.cloud.rms.web.controller.ReservationControllerIT.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.IReservationService;
import com.falconcamp.cloud.rms.domain.service.ReservationNotFoundException;
import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.DEFAULT_ZONE_OFFSET;
import static com.falconcamp.cloud.rms.domain.service.dto.ICampDay.MAX_RESERV_DAYS;
import static com.falconcamp.cloud.rms.web.controller.ReservationDtoDocDescription.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ReservationController.class)
@DisplayName("ReservationController Integration Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationControllerIT {

    private static final String RESERVATION_REQUEST_URI = "/api/v1/resv";
    private static final String AVAILABILITY_REQUEST_URI = "/api/v1/avail";
    private static final String PATH_PARAM_RESERVATION_ID = "id";

    private Supplier<String> specificReservationUriSupplier =
            () -> String.format("%s/{%s}",
                    RESERVATION_REQUEST_URI,
                    PATH_PARAM_RESERVATION_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IReservationService reservationService;

    private UUID id_1;
    private UUID id_2;

    private String fullName_1;
    private String fullName_2;

    private String email_1;
    private String email_2;

    private OffsetDateTime startTime_1;
    private OffsetDateTime startTime_2;

    private OffsetDateTime arrivalDateTime_1;
    private OffsetDateTime arrivalDateTime_2;

    private OffsetDateTime depatureDateTime_1;
    private OffsetDateTime depatureDateTime_2;

    private int days_1;
    private int days_2;

    private ReservationDto dto_1;
    private ReservationDto dto_2;

    @BeforeEach
    void setUp() {

        this.id_1 = UUID.randomUUID();

        this.fullName_1 = RandomStringUtils.randomAlphanumeric(10);
        this.email_1 = "anonymous@tecsys.com";
        this.startTime_1 = OffsetDateTime.of(
                LocalDateTime.of(2020, Month.DECEMBER, 22,
                        0, 0, 0), DEFAULT_ZONE_OFFSET);
        this.arrivalDateTime_1 = this.startTime_1.plusDays(1);
        this.depatureDateTime_1 = this.startTime_1.plusDays(MAX_RESERV_DAYS);

        this.days_1 = MAX_RESERV_DAYS;

        this.dto_1 = ReservationDto.builder()
                .id(this.id_1)
                .fullName(this.fullName_1)
                .email(this.email_1)
                .startDateTime(this.startTime_1)
                .arrivalDateTime(this.arrivalDateTime_1)
                .depatureDateTime(this.depatureDateTime_1)
                .days(this.days_1)
                .build();
    }

    @Test
    void test_Given_ID_When_Geting_Reservation_Then_Having_DTO_Back()
            throws Exception {

        // Given
        String dtoJson = this.objectMapper.writeValueAsString(this.dto_1);

        // When
        given(this.reservationService.getReservation(this.id_1))
                .willReturn(this.dto_1);

        // Then
        this.mockMvc.perform(get(specificReservationUriSupplier.get(), this.id_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(dtoJson))
                .andDo(document("v1/resv-get",
                        pathParameters(parameterWithName(FIELD_NAME_ID)
                                .description("UUID of a Reservation to get")),
                        responseFields(getFieldDescriptors())));

    } // End of test_Given_ID_When_Geting_Reservation_Then_Having_DTO_Back

    @Test
    void test_Given_Nonexisting_ID_When_Geting_Reservation_Then_Handling_NotFoundException()
            throws Exception {

        // Given
        UUID id = UUID.randomUUID();
        willThrow(ReservationNotFoundException.of(id))
                .given(this.reservationService)
                .getReservation(id);

        String expectedErrorMsg = String.format(
                ReservationNotFoundException.ERROR_MSG_TEMPLATE, id.toString());

        // When
        given(this.reservationService.getReservation(this.id_1))
                .willReturn(this.dto_1);

        // Then
        this.mockMvc.perform(get(specificReservationUriSupplier.get(), id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(
                        result.getResolvedException()).isInstanceOf(
                                ReservationNotFoundException.class))
                .andExpect(result -> assertThat(result.getResolvedException()
                        .getMessage()).isEqualTo(expectedErrorMsg));
    }

    @Test
    void test_Given_Resv_Uri_When_Send_Get_Request_Then_Get_All_Reservations() throws Exception {

        // Given
        this.id_2 = UUID.randomUUID();

        this.fullName_2 = RandomStringUtils.randomAlphanumeric(10);
        this.email_2 = "anonymous_2@tecsys.com";
        this.startTime_2 = this.startTime_1.plusDays(7);
        this.arrivalDateTime_2 = this.startTime_2.plusDays(1);
        this.depatureDateTime_2 = this.startTime_2.plusDays(MAX_RESERV_DAYS);
        this.days_2 = MAX_RESERV_DAYS - 1;

        this.dto_2 = ReservationDto.builder()
                .id(this.id_2)
                .fullName(this.fullName_2)
                .email(this.email_2)
                .startDateTime(this.startTime_2)
                .arrivalDateTime(this.arrivalDateTime_2)
                .depatureDateTime(this.depatureDateTime_2)
                .days(this.days_2)
                .build();

        List<ReservationDto> dtos = List.of(this.dto_1, this.dto_2);

        given(this.reservationService.findAllReservations()).willReturn(dtos);

        String dtosJson = this.objectMapper.writeValueAsString(dtos);

        // When & Then
        this.mockMvc.perform(get(RESERVATION_REQUEST_URI))
                .andExpect(status().isOk())
                .andExpect(content().json(dtosJson));
    }

    @Test
    void test_Given_Dto_When_Posting_To_Reservation_Then_Placed_New_Reservation()
            throws Exception {

        // Given
        this.dto_1.setId(null);
        String reservationDtoJson = this.objectMapper.writeValueAsString(this.dto_1);

        this.dto_2 = ReservationDto.builder()
                .id(this.id_1)
                .fullName(this.fullName_1)
                .email(this.email_1)
                .startDateTime(this.startTime_1)
                .arrivalDateTime(this.arrivalDateTime_1)
                .depatureDateTime(this.depatureDateTime_1)
                .days(this.days_1)
                .build();

        given(this.reservationService.save(
                ArgumentMatchers.any(ReservationDto.class)))
                .willReturn(this.dto_2);

        String expectedResponseMsg = String.format(ReservationController
                        .SUCCESSFULLY_PLACED_NEW_RESERVATION_MSG_TEMPLATE,
                this.id_1.toString());

        // When
        this.mockMvc.perform(
                post(RESERVATION_REQUEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(reservationDtoJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponseMsg))
                .andDo(document("v1/resv",  requestFields(
                        getRequestFieldDescriptors())));
    }

}///:~