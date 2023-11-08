package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

@ExtendWith(MockitoExtension.class)
class AviationAerSmallEmittersMonitoringApproachTest {

	private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }
    
    @Test
    void when_annual_emissions_over_only_then_valid() {
        final AviationAerSmallEmittersMonitoringApproach smallEmittersMonitoringApproach = AviationAerSmallEmittersMonitoringApproach.builder()
        		.monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(1L)
                .numOfFlightsMayAug(100L)
                .numOfFlightsSepDec(242L)
                .totalEmissions(BigDecimal.valueOf(26000.56))
                .build();

        final Set<ConstraintViolation<AviationAerSmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(0, violations.size());
    }
    
    @Test
    void when_number_of_flights_over_only_then_valid() {
        final AviationAerSmallEmittersMonitoringApproach smallEmittersMonitoringApproach = AviationAerSmallEmittersMonitoringApproach.builder()
        		.monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(100L)
                .numOfFlightsMayAug(100L)
                .numOfFlightsSepDec(243L)
                .totalEmissions(BigDecimal.valueOf(1000.56))
                .build();

        final Set<ConstraintViolation<AviationAerSmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_both_annual_emissions_and_number_of_flights_over_then_invalid() {
    	final AviationAerSmallEmittersMonitoringApproach smallEmittersMonitoringApproach = AviationAerSmallEmittersMonitoringApproach.builder()
    			.monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(100L)
                .numOfFlightsMayAug(100L)
                .numOfFlightsSepDec(243L)
                .totalEmissions(BigDecimal.valueOf(26000.56))
                .build();

        final Set<ConstraintViolation<AviationAerSmallEmittersMonitoringApproach>> violations = validator.validate(smallEmittersMonitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.monitoringApproach.numberOfFLightsOrTotalEmissions}");
    }
}
