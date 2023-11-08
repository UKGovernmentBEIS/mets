package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach;

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
class SupportFacilityMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_supportFacilityMonitoringApproach() {
        final SupportFacilityMonitoringApproach supportFacilityMonitoringApproach = SupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .explanation("explanation")
                .build();

        final Set<ConstraintViolation<SupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_supportFacilityMonitoringApproach_no_type() {
        final SupportFacilityMonitoringApproach supportFacilityMonitoringApproach = SupportFacilityMonitoringApproach.builder()
                .explanation("explanation")
                .build();

        final Set<ConstraintViolation<SupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_supportFacilityMonitoringApproach_no_explanation() {
        final SupportFacilityMonitoringApproach supportFacilityMonitoringApproach = SupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build();

        final Set<ConstraintViolation<SupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
    }
}
