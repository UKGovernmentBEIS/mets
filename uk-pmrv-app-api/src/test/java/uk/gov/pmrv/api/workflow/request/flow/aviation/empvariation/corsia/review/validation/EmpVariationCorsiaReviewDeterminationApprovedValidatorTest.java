package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

class EmpVariationCorsiaReviewDeterminationApprovedValidatorTest {

	private final EmpVariationCorsiaReviewDeterminationApprovedValidator validator = new EmpVariationCorsiaReviewDeterminationApprovedValidator();

    @Test
    void isValid_true_when_all_mandatory_review_groups_accepted() {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_accepted() {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAmendsNeededReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_have_decision() {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_true_when_all_required_review_groups_have_decision() {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        reviewGroupDecisions.put(EmpCorsiaReviewGroup.METHOD_A_PROCEDURES, buildAcceptedReviewDecision());

        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .methodAProcedures(EmpMethodAProcedures.builder().build())
                    .build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_required_review_groups_have_decision() {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                		.methodAProcedures(EmpMethodAProcedures.builder().build())
                    .build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void getType() {
        assertEquals(EmpVariationDeterminationType.APPROVED, validator.getType());
    }
    
	private Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> buildAcceptedDecisions() {
		Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.OPERATOR_DETAILS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.MONITORING_APPROACH, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.EMISSION_SOURCES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.MANAGEMENT_PROCEDURES, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, buildAcceptedReviewDecision());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.DATA_GAPS, buildAcceptedReviewDecision());
		return reviewGroupDecisions;
	}

    private EmpVariationReviewDecision buildAcceptedReviewDecision() {
        return EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
    }

    private EmpVariationReviewDecision buildAmendsNeededReviewDecision() {
        return EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes").build())
            .build();
    }
}