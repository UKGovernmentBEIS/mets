package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAerRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class AerReInitiateValidator extends RequestCreateAerRelatedValidator {

    public AerReInitiateValidator(final RequestCreateValidatorService requestCreateValidatorService,
                                  final RequestQueryService requestQueryService) {
        super(requestCreateValidatorService, requestQueryService);
    }
    
    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(InstallationAccountStatus.LIVE);
    }

    @Override
    protected RequestType getReferableRequestType() {
        return RequestType.AER;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AER;
    }

	@Override
	protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
		final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
		if (requestDetailsDTO.getRequestStatus() != RequestStatus.COMPLETED) {
			result.setValid(false);
			result.setReportedRequestTypes(Set.of(RequestType.AER));
		}
		return result;
	}
}
