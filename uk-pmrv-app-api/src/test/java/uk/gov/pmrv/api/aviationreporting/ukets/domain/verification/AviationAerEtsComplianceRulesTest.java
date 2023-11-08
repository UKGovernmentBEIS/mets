package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

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
class AviationAerEtsComplianceRulesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_all_rules_are_met_valid() {
        AviationAerEtsComplianceRules etsComplianceRules = AviationAerEtsComplianceRules.builder()
            .monitoringPlanRequirementsMet(true)
            .ukEtsOrderRequirementsMet(true)
            .detailSourceDataVerified(true)
            .partOfSiteVerification("part of site verification")
            .controlActivitiesDocumented(true)
            .proceduresMonitoringPlanDocumented(true)
            .dataVerificationCompleted(true)
            .monitoringApproachAppliedCorrectly(true)
            .flightsCompletenessComparedWithAirTrafficData(true)
            .reportedDataConsistencyChecksPerformed(true)
            .fuelConsistencyChecksPerformed(true)
            .missingDataMethodsApplied(true)
            .regulatorGuidanceMet(true)
            .nonConformities(AviationAerNonConformities.NO)
            .build();

        Set<ConstraintViolation<AviationAerEtsComplianceRules>> violations = validator.validate(etsComplianceRules);

        assertEquals(0, violations.size());
    }

    @Test
    void when_all_rules_are_not_met_valid() {
        AviationAerEtsComplianceRules etsComplianceRules = AviationAerEtsComplianceRules.builder()
            .monitoringPlanRequirementsMet(false)
            .monitoringPlanRequirementsNotMetReason("reason")
            .ukEtsOrderRequirementsMet(false)
            .ukEtsOrderRequirementsNotMetReason("reason")
            .detailSourceDataVerified(false)
            .detailSourceDataNotVerifiedReason("reason")
            .controlActivitiesDocumented(false)
            .controlActivitiesNotDocumentedReason("reason")
            .proceduresMonitoringPlanDocumented(false)
            .proceduresMonitoringPlanNotDocumentedReason("reason")
            .dataVerificationCompleted(false)
            .dataVerificationNotCompletedReason("reason")
            .monitoringApproachAppliedCorrectly(false)
            .monitoringApproachNotAppliedCorrectlyReason("reason")
            .flightsCompletenessComparedWithAirTrafficData(false)
            .flightsCompletenessNotComparedWithAirTrafficDataReason("reason")
            .reportedDataConsistencyChecksPerformed(false)
            .reportedDataConsistencyChecksNotPerformedReason("reason")
            .fuelConsistencyChecksPerformed(false)
            .fuelConsistencyChecksNotPerformedReason("reason")
            .missingDataMethodsApplied(false)
            .missingDataMethodsNotAppliedReason("reason")
            .regulatorGuidanceMet(false)
            .regulatorGuidanceNotMetReason("reason")
            .nonConformities(AviationAerNonConformities.YES)
            .build();

        Set<ConstraintViolation<AviationAerEtsComplianceRules>> violations = validator.validate(etsComplianceRules);

        assertEquals(0, violations.size());
    }

    @Test
    void when_all_rules_are_not_met_and_no_reasons_provided_invalid() {
        AviationAerEtsComplianceRules etsComplianceRules = AviationAerEtsComplianceRules.builder()
            .monitoringPlanRequirementsMet(false)
            .ukEtsOrderRequirementsMet(false)
            .detailSourceDataVerified(false)
            .controlActivitiesDocumented(false)
            .proceduresMonitoringPlanDocumented(false)
            .dataVerificationCompleted(false)
            .monitoringApproachAppliedCorrectly(false)
            .flightsCompletenessComparedWithAirTrafficData(false)
            .reportedDataConsistencyChecksPerformed(false)
            .fuelConsistencyChecksPerformed(false)
            .missingDataMethodsApplied(false)
            .regulatorGuidanceMet(false)
            .nonConformities(AviationAerNonConformities.NOT_APPLICABLE)
            .build();

        Set<ConstraintViolation<AviationAerEtsComplianceRules>> violations = validator.validate(etsComplianceRules);

        assertEquals(12, violations.size());
    }
}