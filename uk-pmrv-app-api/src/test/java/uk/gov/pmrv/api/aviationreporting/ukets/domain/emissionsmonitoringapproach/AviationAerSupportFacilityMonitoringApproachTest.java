package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerSupportFacilityMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_full_scope_flights_then_valid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                .fullScopeTotalEmissions(BigDecimal.valueOf(1234.56))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_full_scope_flights_then_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                .aviationActivityTotalEmissions(BigDecimal.valueOf(1234.56))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.monitoringApproach.fullScopeTotalEmissions}",
                        "{aviationAer.monitoringApproach.aviationActivityTotalEmissions}");
    }

    @Test
    void when_aviation_activity_then_valid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.AVIATION_ACTIVITY)
                .aviationActivityTotalEmissions(BigDecimal.valueOf(234.567))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_aviation_activity_then_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.AVIATION_ACTIVITY)
                .fullScopeTotalEmissions(BigDecimal.valueOf(1234.56))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.monitoringApproach.fullScopeTotalEmissions}",
                        "{aviationAer.monitoringApproach.aviationActivityTotalEmissions}");
    }

    @Test
    void when_full_scope_flights_then_out_of_range_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                .fullScopeTotalEmissions(BigDecimal.valueOf(30000.12))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be less than or equal to 25000.000");
    }

    @Test
    void when_aviation_activity_then_out_of_range_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.AVIATION_ACTIVITY)
                .aviationActivityTotalEmissions(BigDecimal.valueOf(3100.34))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be less than or equal to 3000.000");
    }

    @Test
    void when_full_scope_flights_then_more_decimal_digits_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                .fullScopeTotalEmissions(BigDecimal.valueOf(20000.1234))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void when_aviation_activity_then_more_decimal_digits_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.AVIATION_ACTIVITY)
                .aviationActivityTotalEmissions(BigDecimal.valueOf(100.12345))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
    }

    @Test
    void when_full_scope_flights_zero_total_emissions_invalid() {
        final AviationAerSupportFacilityMonitoringApproach supportFacilityMonitoringApproach = AviationAerSupportFacilityMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                .fullScopeTotalEmissions(BigDecimal.valueOf(0.00))
                .build();

        final Set<ConstraintViolation<AviationAerSupportFacilityMonitoringApproach>> violations = validator.validate(supportFacilityMonitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be greater than 0");
    }
}
