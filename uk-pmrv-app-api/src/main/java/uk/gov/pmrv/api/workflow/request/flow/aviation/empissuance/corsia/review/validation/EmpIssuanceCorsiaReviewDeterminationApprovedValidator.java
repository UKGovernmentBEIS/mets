package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

@Service
public class EmpIssuanceCorsiaReviewDeterminationApprovedValidator
    implements EmpIssuanceCorsiaReviewDeterminationTypeValidator {

    @Override
    public boolean isValid(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return containsDecisionForAllReviewGroups(requestTaskPayload) && isValidReviewGroupDecisionsTaken(requestTaskPayload);
    }

    @Override
    public EmpIssuanceDeterminationType getType() {
        return EmpIssuanceDeterminationType.APPROVED;
    }

    private boolean containsDecisionForAllReviewGroups(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();

        Set<EmpCorsiaReviewGroup> reviewGroups =
            EmpCorsiaReviewUtils.getEmpCorsiaReviewGroups(requestTaskPayload.getEmissionsMonitoringPlan());

        return reviewGroups.stream()
            .filter(reviewGroup -> !reviewGroupDecisions.containsKey(reviewGroup)).findAny()
            .isEmpty();
    }

    private boolean isValidReviewGroupDecisionsTaken(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return requestTaskPayload.getReviewGroupDecisions().values().stream()
            .noneMatch(dec -> dec.getType() != EmpReviewDecisionType.ACCEPTED);
    }
}
