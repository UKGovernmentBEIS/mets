package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;

@Service
public class PermitTransferBReviewDeterminationDeemedWithdrawnAndDecisionsValidator 
    implements PermitReviewDeterminationAndDecisionsValidator<PermitTransferBApplicationReviewRequestTaskPayload> {

    @Override
    public boolean isValid(final PermitTransferBApplicationReviewRequestTaskPayload taskPayload) {
        return true;
    }

    @Override
    public DeterminationType getType() {
        return DeterminationType.DEEMED_WITHDRAWN;
    }

    @Override
	public RequestType getRequestType() {
		return RequestType.PERMIT_TRANSFER_B;
	}

}