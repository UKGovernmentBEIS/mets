package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerOpinionStatementTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_all_rules_are_met_valid() {
        AviationAerOpinionStatement opinionStatement = AviationAerOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
            .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
            .emissionsCorrect(Boolean.TRUE)
            .additionalChangesNotCovered(Boolean.FALSE)
            .siteVisit(AviationAerVirtualSiteVisit.builder().type(AviationAerSiteVisitType.VIRTUAL).reason("reason").build())
            .build();

        Set<ConstraintViolation<AviationAerOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(0, violations.size());
    }

    @Test
    void when_additional_changes_not_covered_details_required_but_not_exist_invalid() {
        AviationAerOpinionStatement opinionStatement = AviationAerOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
            .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
            .emissionsCorrect(Boolean.TRUE)
            .additionalChangesNotCovered(Boolean.TRUE)
            .siteVisit(AviationAerVirtualSiteVisit.builder().type(AviationAerSiteVisitType.VIRTUAL).reason("reason").build())
            .build();

        Set<ConstraintViolation<AviationAerOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(1, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.opinionStatement.additionalChangesNotCovered.details}");
    }

    @Test
    void when_additional_changes_not_covered_details_not_required_but_exist_invalid() {
        AviationAerOpinionStatement opinionStatement = AviationAerOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
            .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
            .emissionsCorrect(Boolean.TRUE)
            .additionalChangesNotCovered(Boolean.FALSE)
            .additionalChangesNotCoveredDetails("details")
            .siteVisit(AviationAerVirtualSiteVisit.builder().type(AviationAerSiteVisitType.VIRTUAL).reason("reason").build())
            .build();

        Set<ConstraintViolation<AviationAerOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(1, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.opinionStatement.additionalChangesNotCovered.details}");
    }

    @Test
    void when_manually_provided_emissions_required_but_not_exist_invalid() {
        AviationAerOpinionStatement opinionStatement = AviationAerOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
            .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
            .emissionsCorrect(Boolean.FALSE)
            .additionalChangesNotCovered(Boolean.FALSE)
            .siteVisit(AviationAerVirtualSiteVisit.builder().type(AviationAerSiteVisitType.VIRTUAL).reason("reason").build())
            .build();

        Set<ConstraintViolation<AviationAerOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(1, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.opinionStatement.emissionsCorrect.manuallyProvidedEmissions}");
    }

    @Test
    void when_manually_provided_emissions_not_required_but_exist_invalid() {
        AviationAerOpinionStatement opinionStatement = AviationAerOpinionStatement.builder()
            .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
            .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
            .emissionsCorrect(Boolean.TRUE)
            .manuallyProvidedEmissions(BigDecimal.TEN)
            .additionalChangesNotCovered(Boolean.FALSE)
            .siteVisit(AviationAerVirtualSiteVisit.builder().type(AviationAerSiteVisitType.VIRTUAL).reason("reason").build())
            .build();

        Set<ConstraintViolation<AviationAerOpinionStatement>> violations = validator.validate(opinionStatement);

        assertEquals(1, violations.size());
        Assertions.assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsOnly("{aviationAerVerificationData.opinionStatement.emissionsCorrect.manuallyProvidedEmissions}");
    }
}