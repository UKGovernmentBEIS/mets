package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecisionType;

@Service
@RequiredArgsConstructor
public class PermitTransferBReviewDeterminationGrantedAndDecisionsValidator implements
    PermitReviewDeterminationAndDecisionsValidator<PermitTransferBApplicationReviewRequestTaskPayload> {

    private final PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return this.containsDecisionForMandatoryGroups(taskPayload) && this.isGrantedDeterminationValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.GRANTED;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_TRANSFER_B;
    }

    private boolean containsDecisionForMandatoryGroups(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
            taskPayload.getPermitTransferDetailsConfirmationDecision() != null;
    }

    private boolean isGrantedDeterminationValid(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        
        return permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload) &&
            taskPayload.getPermitTransferDetailsConfirmationDecision().getType() == PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED;
    }
}
