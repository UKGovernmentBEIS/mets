package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationRejectedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecisionType;

@Service
@RequiredArgsConstructor
public class PermitTransferBReviewDeterminationRejectedAndDecisionsValidator implements
    PermitReviewDeterminationAndDecisionsValidator<PermitTransferBApplicationReviewRequestTaskPayload> {

    private final PermitReviewDeterminationRejectedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationRejectedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return this.containsDecisionForMandatoryGroups(taskPayload) &&
            !this.containsAmendNeededGroups(taskPayload) &&
            this.isRejectedDeterminationValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.REJECTED;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_TRANSFER_B;
    }

    private boolean containsDecisionForMandatoryGroups(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
            taskPayload.getPermitTransferDetailsConfirmationDecision() != null;
    }

    private boolean containsAmendNeededGroups(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return permitReviewGroupsValidator.containsAmendNeededGroups(taskPayload);
    }

    private boolean isRejectedDeterminationValid(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return permitReviewDeterminationRejectedAndDecisionsValidator.isValid(taskPayload) ||
            taskPayload.getPermitTransferDetailsConfirmationDecision().getType() == 
                PermitTransferBDetailsConfirmationReviewDecisionType.REJECTED;
    }

}