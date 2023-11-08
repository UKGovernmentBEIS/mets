package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AviationDreEmissionsCalculationApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_when_type_imposes_explanation_valid() {
        AviationDreEmissionsCalculationApproach dreEmissionsCalculationApproach =
            AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.OTHER_DATASOURCE)
                .otherDataSourceExplanation("explanation")
                .build();

        Set<ConstraintViolation<AviationDreEmissionsCalculationApproach>> violations = validator.validate(dreEmissionsCalculationApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_when_type_imposes_explanation_and_no_explanation_invalid() {
        AviationDreEmissionsCalculationApproach dreEmissionsCalculationApproach =
            AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.OTHER_DATASOURCE)
                .build();

        Set<ConstraintViolation<AviationDreEmissionsCalculationApproach>> violations = validator.validate(dreEmissionsCalculationApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_when_type_not_imposes_explanation_valid() {
        AviationDreEmissionsCalculationApproach dreEmissionsCalculationApproach =
            AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.VERIFIED_ANNUAL_EMISSIONS_REPORT_SUBMITTED_LATE)
                .build();

        Set<ConstraintViolation<AviationDreEmissionsCalculationApproach>> violations = validator.validate(dreEmissionsCalculationApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_when_type_not_imposes_explanation_and_explanation_exist_invalid() {
        AviationDreEmissionsCalculationApproach dreEmissionsCalculationApproach =
            AviationDreEmissionsCalculationApproach.builder()
                .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .otherDataSourceExplanation("explanation")
                .build();

        Set<ConstraintViolation<AviationDreEmissionsCalculationApproach>> violations = validator.validate(dreEmissionsCalculationApproach);

        assertEquals(1, violations.size());
    }

}