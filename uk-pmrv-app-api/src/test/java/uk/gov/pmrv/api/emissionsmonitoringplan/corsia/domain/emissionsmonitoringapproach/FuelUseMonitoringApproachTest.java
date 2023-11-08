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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;

@ExtendWith(MockitoExtension.class)
class FuelUseMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_fuelUseMonitoringApproach() {
        final FuelMonitoringApproach fuelMonitoringApproach = FuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                .build();

        final Set<ConstraintViolation<FuelMonitoringApproach>> violations = validator.validate(fuelMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_fuelUseMonitoringApproach_no_type() {
        final FuelMonitoringApproach fuelMonitoringApproach = FuelMonitoringApproach.builder().build();

        final Set<ConstraintViolation<FuelMonitoringApproach>> violations = validator.validate(fuelMonitoringApproach);

        assertEquals(1, violations.size());
    }
}
