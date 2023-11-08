package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;


@Service
public class EmpVariationCorsiaReviewDeterminationApprovedValidator 
	implements EmpVariationCorsiaReviewDeterminationTypeValidator {

	@Override
    public boolean isValid(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return containsDecisionForAllReviewGroups(requestTaskPayload) && isValidReviewGroupDecisionsTaken(requestTaskPayload);
    }

    @Override
    public EmpVariationDeterminationType getType() {
        return EmpVariationDeterminationType.APPROVED;
    }

    private boolean containsDecisionForAllReviewGroups(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();

        Set<EmpCorsiaReviewGroup> reviewGroups =
            EmpCorsiaReviewUtils.getEmpCorsiaReviewGroups(requestTaskPayload.getEmissionsMonitoringPlan());

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), reviewGroups) &&
            requestTaskPayload.getEmpVariationDetailsReviewDecision() != null;
    }

    private boolean isValidReviewGroupDecisionsTaken(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return (requestTaskPayload.getReviewGroupDecisions().values().stream()
            .noneMatch(dec -> dec.getType() != EmpVariationReviewDecisionType.ACCEPTED) && 
            requestTaskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.ACCEPTED);
    }
}
