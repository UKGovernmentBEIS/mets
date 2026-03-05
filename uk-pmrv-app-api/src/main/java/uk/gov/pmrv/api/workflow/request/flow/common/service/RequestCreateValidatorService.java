package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestCreateValidatorService {

    private final AccountQueryService accountQueryService;
    private final RequestQueryService requestQueryService;

	public RequestCreateValidationResult validate(final Long accountId,
			Set<AccountStatus> applicableAccountStatuses, Set<RequestType> mutuallyExclusiveRequests) {
		final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true)
				.build();
		
		final RequestCreateAccountStatusValidationResult validationAccountStatusesResult = validateAccountStatuses(
				accountId, applicableAccountStatuses);
		if(!validationAccountStatusesResult.isValid()) {
			validationResult.setValid(false);
			validationResult.setApplicableAccountStatuses(applicableAccountStatuses);
			validationResult.setReportedAccountStatus(validationAccountStatusesResult.getReportedAccountStatus());
		}
		
		final RequestCreateRequestTypeValidationResult validationConflictingRequestsTypesResult = validateConflictingRequestTypes(
				accountId, mutuallyExclusiveRequests);
		if(!validationConflictingRequestsTypesResult.isValid()) {
			validationResult.setValid(false);
			validationResult.setReportedRequestTypes(validationConflictingRequestsTypesResult.getReportedRequestTypes());
		}
		
		return validationResult;
	}
    
	public RequestCreateAccountStatusValidationResult validateAccountStatuses(final Long accountId,
			Set<AccountStatus> applicableAccountStatuses) {
		final RequestCreateAccountStatusValidationResult validationResult = RequestCreateAccountStatusValidationResult.builder().valid(true)
				.build();
		final AccountStatus accountStatus = accountQueryService.getAccountStatus(accountId);

		final boolean validAccountStatus = applicableAccountStatuses.isEmpty()
				|| applicableAccountStatuses.contains(accountStatus);
		if (!validAccountStatus) {
			validationResult.setValid(false);
			validationResult.setReportedAccountStatus(accountStatus);
		}

		return validationResult;
	}
	
	public RequestCreateRequestTypeValidationResult validateConflictingRequestTypes(final Long accountId,
			Set<RequestType> mutuallyExclusiveRequestsTypes) {
		final RequestCreateRequestTypeValidationResult validationResult = RequestCreateRequestTypeValidationResult.builder().valid(true)
				.build();

		if (!mutuallyExclusiveRequestsTypes.isEmpty()) {
			final List<Request> inProgressRequests = requestQueryService.findInProgressRequestsByAccount(accountId);
			final Set<RequestType> conflictingRequests = inProgressRequests.stream().map(Request::getType)
					.filter(mutuallyExclusiveRequestsTypes::contains).collect(Collectors.toSet());

			if (!conflictingRequests.isEmpty()) {
				validationResult.setValid(false);
				validationResult.setReportedRequestTypes(conflictingRequests);
			}
		}

		return validationResult;
	}
}
