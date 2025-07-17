package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class DoalRequestCreateActionPayloadTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validDoalRequestCreateActionPayload() {
        DoalRequestCreateActionPayload actionPayload = DoalRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.DOAL_REQUEST_CREATE_ACTION_PAYLOAD)
                .year(Year.of(2020))
                .build();
        Set<ConstraintViolation<DoalRequestCreateActionPayload>> violations = validator.validate(actionPayload);

        assertThat(violations.isEmpty());
    }

    @Test
    void validDoalRequestCreateActionPayload_not_valid_year() {
        DoalRequestCreateActionPayload actionPayload = DoalRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.DOAL_REQUEST_CREATE_ACTION_PAYLOAD)
                .year(Year.of(2019))
                .build();
        Set<ConstraintViolation<DoalRequestCreateActionPayload>> violations = validator.validate(actionPayload);

        assertThat(violations).hasSize(1);
    }

    @Test
    void validDoalRequestCreateActionPayload_null_year() {
        DoalRequestCreateActionPayload actionPayload = DoalRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.DOAL_REQUEST_CREATE_ACTION_PAYLOAD)
                .build();
        Set<ConstraintViolation<DoalRequestCreateActionPayload>> violations = validator.validate(actionPayload);

        assertThat(violations).hasSize(1);
    }
}
