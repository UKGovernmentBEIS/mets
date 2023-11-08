package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

class EmpVariationUkEtsReviewDeterminationApprovedValidatorTest {

	private final EmpVariationUkEtsReviewDeterminationApprovedValidator validator = new EmpVariationUkEtsReviewDeterminationApprovedValidator();

    @Test
    void isValid_true_when_all_mandatory_review_groups_accepted() {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_accepted() {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAmendsNeededReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_mandatory_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_true_when_all_required_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        reviewGroupDecisions.put(EmpUkEtsReviewGroup.DATA_GAPS, buildAcceptedReviewDecision());

        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .dataGaps(EmpDataGaps.builder().build())
                    .build())
                .build();

        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void isValid_false_when_not_all_required_review_groups_have_decision() {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = buildAcceptedDecisions();

        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        	EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(buildAcceptedReviewDecision())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                	.dataGaps(EmpDataGaps.builder().build())
                    .build())
                .build();

        assertFalse(validator.isValid(requestTaskPayload));
    }

    @Test
    void getType() {
        assertEquals(EmpVariationDeterminationType.APPROVED, validator.getType());
    }
    
	private Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> buildAcceptedDecisions() {
		Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
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
