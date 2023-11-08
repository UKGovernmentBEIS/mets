package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpIssuanceUkEtsReviewDeterminationApprovedValidatorTest {

    private final EmpIssuanceUkEtsReviewDeterminationApprovedValidator validator = new EmpIssuanceUkEtsReviewDeterminationApprovedValidator();

    @Test
    void isValid_true_when_all_mandatory_review_groups_accepted() {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.LATE_SUBMISSION, buildAcceptedReviewDecision());

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_accepted() {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.LATE_SUBMISSION, buildAmendsNeededReviewDecision());

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_true_when_all_required_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.LATE_SUBMISSION, buildAcceptedReviewDecision());

        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FUEL_UPLIFT_PROCEDURES, buildAcceptedReviewDecision());

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder().build())
                    .build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_required_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.LATE_SUBMISSION, buildAcceptedReviewDecision());

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder().build())
                    .build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void getType() {
        assertEquals(EmpIssuanceDeterminationType.APPROVED, validator.getType());
    }

    private EmpIssuanceReviewDecision buildAcceptedReviewDecision() {
        return EmpIssuanceReviewDecision.builder()
            .type(EmpReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
    }

    private EmpIssuanceReviewDecision buildAmendsNeededReviewDecision() {
        return EmpIssuanceReviewDecision.builder()
            .type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes").build())
            .build();
    }
}