package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertEmissionsType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;

@ExtendWith(MockitoExtension.class)
class CertMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_certMonitoringApproach() {
        final CertMonitoringApproach certMonitoringApproach = CertMonitoringApproach.builder()
            .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
            .certEmissionsType(CertEmissionsType.GREAT_CIRCLE_DISTANCE)
            .explanation("explanation")
            .build();

        final Set<ConstraintViolation<CertMonitoringApproach>> violations = validator.validate(certMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_certMonitoringApproach_no_type() {
        final CertMonitoringApproach certMonitoringApproach = CertMonitoringApproach.builder()
            .certEmissionsType(CertEmissionsType.GREAT_CIRCLE_DISTANCE)
            .explanation("explanation")
            .build();

        final Set<ConstraintViolation<CertMonitoringApproach>> violations = validator.validate(certMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certMonitoringApproach_no_explanation() {
        final CertMonitoringApproach certMonitoringApproach = CertMonitoringApproach.builder()
            .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
            .certEmissionsType(CertEmissionsType.GREAT_CIRCLE_DISTANCE)
            .build();

        final Set<ConstraintViolation<CertMonitoringApproach>> violations = validator.validate(certMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_certMonitoringApproach_no_certEmissionsType() {
        final CertMonitoringApproach certMonitoringApproach = CertMonitoringApproach.builder()
            .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
            .explanation("explanation")
            .build();

        final Set<ConstraintViolation<CertMonitoringApproach>> violations = validator.validate(certMonitoringApproach);

        assertEquals(1, violations.size());
    }
}
