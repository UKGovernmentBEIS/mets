package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TechnicalCapabilityDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void valid() {
        TechnicalCapabilityDetails technicalCapabilityDetails =
                getTechnicalCapabilityDetails(TechnicalCapability.RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES, "details");

        Set<ConstraintViolation<TechnicalCapabilityDetails>> violations = validator.validate(technicalCapabilityDetails);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void invalidIfTechnicalCapabilityNull() {
        TechnicalCapabilityDetails technicalCapabilityDetails =
                getTechnicalCapabilityDetails(null, "details");

        Set<ConstraintViolation<TechnicalCapabilityDetails>> violations = validator.validate(technicalCapabilityDetails);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidIfDetailsNull() {
        TechnicalCapabilityDetails technicalCapabilityDetails =
                getTechnicalCapabilityDetails(TechnicalCapability.RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES, null);

        Set<ConstraintViolation<TechnicalCapabilityDetails>> violations = validator.validate(technicalCapabilityDetails);
        assertThat(violations.size()).isEqualTo(1);
    }

    TechnicalCapabilityDetails getTechnicalCapabilityDetails(TechnicalCapability technicalCapability, String details){
        return TechnicalCapabilityDetails.builder()
                .technicalCapability(technicalCapability)
                .details(details)
                .build();
    }
}
