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
class SmallEmittersMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_smallEmittersMonitoringApproach() {
        final SmallEmittersMonitoringApproach smallEmittersMonitoringApproach = SmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .explanation("explanation")
                .build();

        final Set<ConstraintViolation<SmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_smallEmittersMonitoringApproach_no_type() {
        final SmallEmittersMonitoringApproach smallEmittersMonitoringApproach = SmallEmittersMonitoringApproach.builder()
                .explanation("explanation")
                .build();

        final Set<ConstraintViolation<SmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_smallEmittersMonitoringApproach_no_explanation() {
        final SmallEmittersMonitoringApproach smallEmittersMonitoringApproach = SmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .build();

        final Set<ConstraintViolation<SmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(1, violations.size());
    }
}
