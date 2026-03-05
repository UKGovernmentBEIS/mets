package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.util.Set;

@Service
public class DreInitiateValidator extends RequestCreateAerRelatedValidator {

	public DreInitiateValidator(RequestCreateValidatorService requestCreateValidatorService,
			RequestQueryService requestQueryService) {
		super(requestCreateValidatorService, requestQueryService);
	}

	@Override
	protected Set<AccountStatus> getApplicableAccountStatuses() {
		return Set.of(
				InstallationAccountStatus.LIVE,
				InstallationAccountStatus.AWAITING_REVOCATION,
				InstallationAccountStatus.AWAITING_SURRENDER);
	}

	@Override
	protected RequestType getReferableRequestType() {
		return RequestType.AER;
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.DRE;
	}

	@Override
	protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
		final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
		AerRequestMetadata metadata = (AerRequestMetadata) requestDetailsDTO.getRequestMetadata();
		if(metadata == null) {
			throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestDetailsDTO.getId());
		}
		
		if (requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.DRE,
				RequestStatus.IN_PROGRESS, accountId, metadata.getYear())) {
			result.setValid(false);
			result.setReportedRequestTypes(Set.of(RequestType.DRE));
		}
		
		return result;
	}

}
