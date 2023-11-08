package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@RequiredArgsConstructor
public abstract class RequestCreateAerRelatedValidator implements RequestCreateByRequestValidator<ReportRelatedRequestCreateActionPayload> {

    private final RequestCreateValidatorService requestCreateValidatorService;
    protected final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId, ReportRelatedRequestCreateActionPayload payload) {
    	RequestDetailsDTO requestDetailsDTO = requestQueryService.findRequestDetailsById(payload.getRequestId());
    	
    	if(requestDetailsDTO.getRequestType() != this.getReferableRequestType()) {
    		throw new BusinessException(ErrorCode.AER_REQUEST_IS_NOT_AER, payload.getRequestId());
    	}
    	
    	final RequestCreateValidationResult overallValidationResult = RequestCreateValidationResult.builder().valid(true).build();
    	
    	RequestCreateAccountStatusValidationResult accountStatusValidationResult = requestCreateValidatorService
                .validateAccountStatuses(accountId, this.getApplicableAccountStatuses());
    	if(!accountStatusValidationResult.isValid()) {
    		overallValidationResult.setValid(false);
    		overallValidationResult.setReportedAccountStatus(accountStatusValidationResult.getReportedAccountStatus());
    		overallValidationResult.setApplicableAccountStatuses(this.getApplicableAccountStatuses());
    	}
    	
    	RequestCreateRequestTypeValidationResult requestTypeValidationResult = this.validateRequestType(accountId, requestDetailsDTO);
    	if(!requestTypeValidationResult.isValid()) {
    		overallValidationResult.setValid(false);
    		overallValidationResult.setReportedRequestTypes(requestTypeValidationResult.getReportedRequestTypes());
    	}
    	
    	return overallValidationResult;
    }
    
    protected abstract RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO);

    protected abstract Set<AccountStatus> getApplicableAccountStatuses();

	protected abstract RequestType getReferableRequestType();
}
