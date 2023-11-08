package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproachType;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaOpinionStatementTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_all_rules_are_met_valid() {
        AviationAerCorsiaOpinionStatement opinionStatement = AviationAerCorsiaOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerCorsiaFuelType.JET_KEROSENE))
            .monitoringApproachType(AviationAerCorsiaMonitoringApproachType.CERT_MONITORING)
            .emissionsCorrect(Boolean.TRUE)
            .build();

        Set<ConstraintViolation<AviationAerCorsiaOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(0, violations.size());
    }

    @Test
    void when_manually_provided_emissions_required_but_not_exist_invalid() {
        AviationAerCorsiaOpinionStatement opinionStatement = AviationAerCorsiaOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerCorsiaFuelType.TS_1))
            .monitoringApproachType(AviationAerCorsiaMonitoringApproachType.CERT_MONITORING)
            .emissionsCorrect(Boolean.FALSE)
            .build();

        Set<ConstraintViolation<AviationAerCorsiaOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(2, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyInternationalFlights}",
                "{aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyOffsettingFlights}");
    }

    @Test
    void when_manually_provided_emissions_not_required_but_exist_invalid() {
        AviationAerCorsiaOpinionStatement opinionStatement = AviationAerCorsiaOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerCorsiaFuelType.TS_1))
            .monitoringApproachType(AviationAerCorsiaMonitoringApproachType.CERT_MONITORING)
            .emissionsCorrect(Boolean.TRUE)
            .manuallyOffsettingFlightsProvidedEmissions(BigDecimal.TEN)
            .manuallyInternationalFlightsProvidedEmissions(BigDecimal.ONE)
            .build();

        Set<ConstraintViolation<AviationAerCorsiaOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(2, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyInternationalFlights}",
                "{aviationAerVerificationData.corsia.verifierDetails.opinionStatement.emissionsCorrect.manuallyOffsettingFlights}");
    }
}