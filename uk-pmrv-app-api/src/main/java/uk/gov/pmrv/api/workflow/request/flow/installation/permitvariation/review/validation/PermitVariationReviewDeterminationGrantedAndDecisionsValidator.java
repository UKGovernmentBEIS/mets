package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewDeterminationGrantedAndDecisionsValidator implements
    PermitReviewDeterminationAndDecisionsValidator<PermitVariationApplicationReviewRequestTaskPayload> {

    private final PermitReviewDeterminationGrantedAndDecisionsValidator<PermitVariationReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitVariationReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return containsDecisionForMandatoryGroups(taskPayload) &&
            isGrantedDeterminationValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.GRANTED;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_VARIATION;
    }

    private boolean containsDecisionForMandatoryGroups(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
            taskPayload.getPermitVariationDetailsReviewDecision() != null;
    }

    private boolean isGrantedDeterminationValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload) &&
            taskPayload.getPermitVariationDetailsReviewDecision().getType() == ReviewDecisionType.ACCEPTED;
    }

}
