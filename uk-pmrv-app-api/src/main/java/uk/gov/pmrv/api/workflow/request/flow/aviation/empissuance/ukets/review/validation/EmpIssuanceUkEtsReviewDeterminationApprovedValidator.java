package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import java.util.Map;
import java.util.Set;

@Service
public class EmpIssuanceUkEtsReviewDeterminationApprovedValidator
    implements EmpIssuanceUkEtsReviewDeterminationTypeValidator {

    @Override
    public boolean isValid(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return containsDecisionForAllReviewGroups(requestTaskPayload) && isValidReviewGroupDecisionsTaken(requestTaskPayload);
    }

    @Override
    public EmpIssuanceDeterminationType getType() {
        return EmpIssuanceDeterminationType.APPROVED;
    }

    private boolean containsDecisionForAllReviewGroups(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();

        Set<EmpUkEtsReviewGroup> reviewGroups =
            EmpUkEtsReviewUtils.getEmpUkEtsReviewGroups(requestTaskPayload.getEmissionsMonitoringPlan());

        return CollectionUtils.isEqualCollection(reviewGroupDecisions.keySet(), reviewGroups);
    }

    private boolean isValidReviewGroupDecisionsTaken(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return requestTaskPayload.getReviewGroupDecisions().values().stream()
            .noneMatch(dec -> dec.getType() != EmpReviewDecisionType.ACCEPTED);
    }
}
