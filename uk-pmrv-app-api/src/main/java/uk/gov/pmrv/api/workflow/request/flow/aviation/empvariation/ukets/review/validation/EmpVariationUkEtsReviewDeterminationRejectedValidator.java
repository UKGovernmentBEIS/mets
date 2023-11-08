package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

import java.util.Map;
import java.util.Set;

@Service
public class EmpVariationUkEtsReviewDeterminationRejectedValidator 
	implements EmpVariationUkEtsReviewDeterminationTypeValidator {

	@Override
    public boolean isValid(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
		return containsDecisionForAllReviewGroups(requestTaskPayload) 
				&& !containsAmendNeededGroups(requestTaskPayload)
				&& isValidReviewGroupDecisionsTaken(requestTaskPayload);
    }

    @Override
    public EmpVariationDeterminationType getType() {
        return EmpVariationDeterminationType.REJECTED;
    }
    
    private boolean containsDecisionForAllReviewGroups(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();

        Set<EmpUkEtsReviewGroup> reviewGroups =
            EmpUkEtsReviewUtils.getEmpUkEtsReviewGroups(requestTaskPayload.getEmissionsMonitoringPlan());

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), reviewGroups) &&
            requestTaskPayload.getEmpVariationDetailsReviewDecision() != null;
    }
    
    private boolean containsAmendNeededGroups(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return (requestTaskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(dec -> dec.getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED) ||
            requestTaskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED);
    }

    private boolean isValidReviewGroupDecisionsTaken(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return (requestTaskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(dec -> dec.getType() == EmpVariationReviewDecisionType.REJECTED) ||
            requestTaskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.REJECTED);
    }
}
