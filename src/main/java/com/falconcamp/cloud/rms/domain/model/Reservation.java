//: com.falconcamp.cloud.rms.domain.model.Reservation.java


package com.falconcamp.cloud.rms.domain.model;


import lombok.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder @AllArgsConstructor
public class Reservation implements Comparable<Reservation> {

    static final long serialVersionUID = -5815566940065181210L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Column(length = 64, columnDefinition = "varchar(64)")
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true, updatable = false)
    private OffsetDateTime startDateTime;

    private OffsetDateTime arrivalDateTime;

    private OffsetDateTime detatureDateTime;

    private int days;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reservation)) {
            return false;
        }

        final Reservation other = (Reservation) obj;

        return new EqualsBuilder()
                .append(this.startDateTime, other.startDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.startDateTime).toHashCode();
    }

    @Override
    public int compareTo(final Reservation obj) {
        return new CompareToBuilder()
                .append(this.startDateTime, obj.startDateTime)
                .toComparison();
    }

}///:~