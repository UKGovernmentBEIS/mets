package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class PermitVariationCreateValidator extends RequestCreateAccountRelatedValidator {

	public PermitVariationCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
		super(requestCreateValidatorService);
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.PERMIT_VARIATION;
	}

	@Override
	protected Set<AccountStatus> getApplicableAccountStatuses() {
		return Set.of(InstallationAccountStatus.LIVE);
	}

	@Override
	protected Set<RequestType> getMutuallyExclusiveRequests() {
		return Set.of(RequestType.PERMIT_VARIATION, RequestType.PERMIT_TRANSFER_A, RequestType.PERMIT_SURRENDER);
	}

}
