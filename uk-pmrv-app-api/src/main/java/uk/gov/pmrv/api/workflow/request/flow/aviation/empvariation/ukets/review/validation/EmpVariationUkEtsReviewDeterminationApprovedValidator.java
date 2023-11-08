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
public class EmpVariationUkEtsReviewDeterminationApprovedValidator 
	implements EmpVariationUkEtsReviewDeterminationTypeValidator {

	@Override
    public boolean isValid(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return containsDecisionForAllReviewGroups(requestTaskPayload) && isValidReviewGroupDecisionsTaken(requestTaskPayload);
    }

    @Override
    public EmpVariationDeterminationType getType() {
        return EmpVariationDeterminationType.APPROVED;
    }

    private boolean containsDecisionForAllReviewGroups(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();

        Set<EmpUkEtsReviewGroup> reviewGroups =
            EmpUkEtsReviewUtils.getEmpUkEtsReviewGroups(requestTaskPayload.getEmissionsMonitoringPlan());

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), reviewGroups) &&
            requestTaskPayload.getEmpVariationDetailsReviewDecision() != null;
    }

    private boolean isValidReviewGroupDecisionsTaken(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return (requestTaskPayload.getReviewGroupDecisions().values().stream()
            .noneMatch(dec -> dec.getType() != EmpVariationReviewDecisionType.ACCEPTED) && 
            requestTaskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.ACCEPTED);
    }
}
