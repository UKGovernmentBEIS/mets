package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationRejectedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewDeterminationRejectedAndDecisionsValidator implements
    PermitReviewDeterminationAndDecisionsValidator<PermitVariationApplicationReviewRequestTaskPayload> {

    private final PermitReviewDeterminationRejectedAndDecisionsValidator<PermitVariationReviewDecision> permitReviewDeterminationRejectedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitVariationReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return containsDecisionForMandatoryGroups(taskPayload) &&
            !containsAmendNeededGroups(taskPayload) &&
            isRejectedDeterminationValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.REJECTED;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_VARIATION;
    }

    private boolean containsDecisionForMandatoryGroups(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
            taskPayload.getPermitVariationDetailsReviewDecision() != null;
    }

    private boolean containsAmendNeededGroups(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewGroupsValidator.containsAmendNeededGroups(taskPayload) ||
            taskPayload.getPermitVariationDetailsReviewDecision().getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED;
    }

    private boolean isRejectedDeterminationValid(PermitVariationApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewDeterminationRejectedAndDecisionsValidator.isValid(taskPayload) ||
            taskPayload.getPermitVariationDetailsReviewDecision().getType() == ReviewDecisionType.REJECTED;
    }

}