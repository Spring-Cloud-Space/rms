//: com.falconcamp.cloud.rms.web.controller.ReservationDtoDocDescription.java


package com.falconcamp.cloud.rms.web.controller;


import com.falconcamp.cloud.rms.domain.service.dto.ReservationDto;
import lombok.NonNull;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;


public class ReservationDtoDocDescription {

    static final String FIELD_NAME_ID = "id";
    static final String FIELD_NAME_VERSION = "version";
    static final String FIELD_NAME_CREATED_DATE = "createdDate";
    static final String FIELD_NAME_LAST_MODIFIED_DATE = "lastModifiedDate";
    static final String FIELD_NAME_FULL_NAME = "fullName";
    static final String FIELD_NAME_EMAIL = "email";
    static final String FIELD_NAME_START_DATE_TIME = "startDateTime";
    static final String FIELD_NAME_ARRIVAL_DATE_TIME = "arrivalDateTime";
    static final String FIELD_NAME_DEPATURE_DATE_TIME = "depatureDateTime";
    static final String FIELD_NAME_DAYS = "days";

    static final List<String> FIELDS = List.of(
            FIELD_NAME_ID,
            FIELD_NAME_VERSION,
            FIELD_NAME_CREATED_DATE,
            FIELD_NAME_LAST_MODIFIED_DATE,
            FIELD_NAME_FULL_NAME,
            FIELD_NAME_EMAIL,
            FIELD_NAME_START_DATE_TIME,
            FIELD_NAME_ARRIVAL_DATE_TIME,
            FIELD_NAME_DEPATURE_DATE_TIME,
            FIELD_NAME_DAYS);

    static final Map<String, String> FIELD_DESC_MAP =
            Map.of(FIELD_NAME_ID, "Id of Reservation",
                    FIELD_NAME_VERSION, "Version number",
                    FIELD_NAME_CREATED_DATE, "Creating time",
                    FIELD_NAME_LAST_MODIFIED_DATE, "Recent modified time",
                    FIELD_NAME_FULL_NAME, "Customer's full name",
                    FIELD_NAME_EMAIL, "Customer's email",
                    FIELD_NAME_START_DATE_TIME, "Start time",
                    FIELD_NAME_ARRIVAL_DATE_TIME, "Arrive time",
                    FIELD_NAME_DEPATURE_DATE_TIME, "Depature time",
                    FIELD_NAME_DAYS, "The number of reserved days");

    static final Map<String, Class> FIELD_TYPE_MAP =
            Map.of(FIELD_NAME_ID, UUID.class,
                    FIELD_NAME_VERSION, Integer.class,
                    FIELD_NAME_CREATED_DATE, OffsetDateTime.class,
                    FIELD_NAME_LAST_MODIFIED_DATE, OffsetDateTime.class,
                    FIELD_NAME_FULL_NAME, String.class,
                    FIELD_NAME_EMAIL, String.class,
                    FIELD_NAME_START_DATE_TIME, OffsetDateTime.class,
                    FIELD_NAME_ARRIVAL_DATE_TIME, OffsetDateTime.class,
                    FIELD_NAME_DEPATURE_DATE_TIME, OffsetDateTime.class,
                    FIELD_NAME_DAYS, Integer.class);

    static final Map<String, UnaryOperator<FieldDescriptor>>
            REQUEST_FIELD_DESCRIPTOR_MAP =
            Map.of(FIELD_NAME_ID, FieldDescriptor::ignored,
                    FIELD_NAME_VERSION, FieldDescriptor::ignored,
                    FIELD_NAME_CREATED_DATE, FieldDescriptor::ignored,
                    FIELD_NAME_LAST_MODIFIED_DATE, FieldDescriptor::ignored,
                    FIELD_NAME_FULL_NAME,
                    fd -> fd.description(FIELD_DESC_MAP.get(FIELD_NAME_FULL_NAME)),
                    FIELD_NAME_EMAIL,
                    fd -> fd.description(FIELD_DESC_MAP.get(FIELD_NAME_EMAIL)),
                    FIELD_NAME_START_DATE_TIME,
                    fd -> fd.description(FIELD_DESC_MAP.get(
                            FIELD_NAME_START_DATE_TIME)),
                    FIELD_NAME_ARRIVAL_DATE_TIME,
                    fd -> fd.description(FIELD_DESC_MAP.get(
                            FIELD_NAME_ARRIVAL_DATE_TIME)),
                    FIELD_NAME_DEPATURE_DATE_TIME,
                    fd -> fd.description(FIELD_DESC_MAP.get(
                            FIELD_NAME_DEPATURE_DATE_TIME)),
                    FIELD_NAME_DAYS,
                    fd -> fd.description(FIELD_DESC_MAP.get(FIELD_NAME_DAYS)));

    static FieldDescriptor[] getFieldDescriptors() {
        return FIELDS.stream()
                .map(ReservationDtoDocDescription::createFieldDescriptors)
                .toArray(FieldDescriptor[]::new);
    }

    static FieldDescriptor[] getRequestFieldDescriptors() {

        ConstrainedFields constraintFields = ConstrainedFields.of(
                ReservationDto.class);

        return FIELDS.stream()
                .map(field -> constraintFields.withPath(field,
                        REQUEST_FIELD_DESCRIPTOR_MAP.get(field)))
                .toArray(FieldDescriptor[]::new);
    }

    private static FieldDescriptor createFieldDescriptors(@NonNull String field) {
        return fieldWithPath(field)
                .description(FIELD_DESC_MAP.get(field))
                .type(FIELD_TYPE_MAP.get(field));
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDesc;

        private ConstrainedFields(Class<?> input) {
            this.constraintDesc = new ConstraintDescriptions(input);
        }

        static ReservationDtoDocDescription.ConstrainedFields of(Class<?> input) {
            return new ReservationDtoDocDescription.ConstrainedFields(input);
        }

        private FieldDescriptor withPath(
                @NonNull String path,
                @NonNull UnaryOperator<FieldDescriptor> descFunc) {

            List<String> descs = this.constraintDesc.descriptionsForProperty(path);
            String allDescs = StringUtils.collectionToDelimitedString(
                    descs, ". ");

            return descFunc.apply(fieldWithPath(path).attributes(
                    key("constraints").value(allDescs)));
        }

    } //: End of class ConstrainedFields

}///:~