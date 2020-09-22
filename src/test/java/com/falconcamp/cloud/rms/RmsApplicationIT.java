//: com.falconcamp.cloud.rms.RmsApplicationIT.java


package com.falconcamp.cloud.rms;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import com.falconcamp.cloud.rms.domain.repository.IReservationRepository;
import com.falconcamp.cloud.rms.domain.service.dto.ICampDay;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


//@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Programmatic-Transaction-Management Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class RmsApplicationIT {

    /*
     * The PlatformTransactionManager helps the template to
     *   - Create
     *   - Commit
     *   - Rollback
     * Transactions
     */
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private IReservationRepository reservationRepository;

    private TransactionTemplate transactionTemplate;


    @BeforeEach
    void setUp() {
        this.transactionTemplate = new TransactionTemplate(this.transactionManager);
    }

    @Test
    void test_Injected_PlatformTransactionManager() {

        // Given
        int expectedCount = 4;

        this.transactionTemplate.setIsolationLevel(
                TransactionDefinition.ISOLATION_SERIALIZABLE);

        // When
        List<Reservation> allReservation =
                this.transactionTemplate.execute(status -> {
                    return this.reservationRepository.findAll();
                });

        long countInAug = allReservation.stream()
                .filter(reservation -> reservation.getStartDateTime().isBefore(
                        OffsetDateTime.of(2020, 10, 1,
                                0, 0, 0, 0,
                                ICampDay.DEFAULT_ZONE_OFFSET)))
                .count();

        // Then
        assertThat(allReservation.size()).isEqualTo(expectedCount);
        assertThat(countInAug).isEqualTo(1);
    }

}