package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceDeterminationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void valid_when_approved_with_reason() {
        final EmpIssuanceDetermination empIssuanceDetermination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("value")
            .build();

        final Set<ConstraintViolation<EmpIssuanceDetermination>> violations = validator.validate(empIssuanceDetermination);

        assertEquals(0, violations.size());
    }

    @Test
    void valid_when_approved_without_reason() {
        final EmpIssuanceDetermination empIssuanceDetermination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .build();

        final Set<ConstraintViolation<EmpIssuanceDetermination>> violations = validator.validate(empIssuanceDetermination);

        assertEquals(0, violations.size());
    }

    @Test
    void valid_when_deemed_withdrawn_with_reason() {
        final EmpIssuanceDetermination empIssuanceDetermination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN)
            .reason("value")
            .build();

        final Set<ConstraintViolation<EmpIssuanceDetermination>> violations = validator.validate(empIssuanceDetermination);

        assertEquals(0, violations.size());
    }

    @Test
    void invalid_when_deemed_withdrawn_without_reason() {
        final EmpIssuanceDetermination empIssuanceDetermination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN)
            .build();

        final Set<ConstraintViolation<EmpIssuanceDetermination>> violations = validator.validate(empIssuanceDetermination);

        assertEquals(1, violations.size());
    }

}