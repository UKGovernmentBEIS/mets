package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationRejectedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;

@Service
@RequiredArgsConstructor
public class PermitIssuanceReviewDeterminationRejectedAndDecisionsValidator implements PermitReviewDeterminationAndDecisionsValidator<PermitIssuanceApplicationReviewRequestTaskPayload> {

	private final PermitReviewDeterminationRejectedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationRejectedAndDecisionsValidator;
    private final PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;

    @Override
    public boolean isValid(PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        return permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload) &&
        		!permitReviewGroupsValidator.containsAmendNeededGroups(taskPayload) &&
        		permitReviewDeterminationRejectedAndDecisionsValidator.isValid(taskPayload);
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.REJECTED;
    }
    
    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_ISSUANCE;
	}
    
}