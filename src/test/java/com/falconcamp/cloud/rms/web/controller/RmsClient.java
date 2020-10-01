//: com.falconcamp.cloud.rms.web.controller.RmsClient.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


@AllArgsConstructor(staticName = "of")
final class RmsClient {

    private final String url;
    private final TestRestTemplate restTemplate;
    private final ReservationDto dto;

    ResponseEntity<String> placeNewReservation() {
        return this.restTemplate.postForEntity(this.url, this.dto, String.class);
    }

    ResponseEntity<String> updateReservation() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDto> entity = new HttpEntity<>(
                dto, headers);
        return this.restTemplate.exchange(this.url, HttpMethod.PUT,
                entity, String.class);
    }

}///:~