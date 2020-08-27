//: com.falconcamp.cloud.rms.domain.repository.IReservationRepository.java


package com.falconcamp.cloud.rms.domain.repository;


import com.falconcamp.cloud.rms.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IReservationRepository extends JpaRepository<Reservation, UUID> {

    /*
     * LockModeType.OPTIMISTIC_FORCE_INCREMENT
     *   - 	Always increases the entity version
     *      (even when the entity doesn’t change) and issues a version check
     *      upon transaction commit, therefore ensuring optimistic locking
     *      repeatable reads.
     *   - Same as LockModeType.WRITE
     *
     * LockModeType.OPTIMISTIC
     *   - Always issues a version check upon transaction commit,
     *     therefore ensuring optimistic locking repeatable reads.
     *   - Same as LockModeType.READ
     *
     * LockModeType.PESSIMISTIC_READ
     *   - A shared lock is acquired to prevent any other transaction from
     *     acquiring a PESSIMISTIC_WRITE lock
     *
     * LockModeType.PESSIMISTIC_WRITE
     *   - An exclusive lock is acquired to prevent any other transaction from
     *     acquiring a PESSIMISTIC_READ or a PESSIMISTIC_WRITE lock
     *
     * LockModeType.PESSIMISTIC_FORCE_INCREMENT
     *   - A database lock is acquired to prevent any other transaction from
     *     acquiring a PESSIMISTIC_READ or a PESSIMISTIC_WRITE lock and the
     *     entity version is incremented upon transaction commit
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Reservation> findById(UUID id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    List<Reservation> findAllByStartDateTimeBetween(
            OffsetDateTime startDateTime, OffsetDateTime endDateTime);

}///:~