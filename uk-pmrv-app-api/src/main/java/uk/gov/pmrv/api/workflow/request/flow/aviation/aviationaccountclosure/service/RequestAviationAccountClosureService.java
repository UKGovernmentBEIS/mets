package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmittedRequestActionPayload;


@Service
@RequiredArgsConstructor
public class RequestAviationAccountClosureService {
	
	private final RequestService requestService;
	private final AviationAccountUpdateService aviationAccountUpdateService;
	private final AviationAccountClosureValidatorService validatorService;
	
	@Transactional
    public void applySavePayload(AviationAccountClosureSaveRequestTaskActionPayload actionPayload, 
    		RequestTask requestTask) {

		AviationAccountClosureSubmitRequestTaskPayload taskPayload =
				(AviationAccountClosureSubmitRequestTaskPayload) requestTask.getPayload();

		taskPayload.setAviationAccountClosure(actionPayload.getAviationAccountClosure());
	}

	@Transactional
	public void applySubmitAction(RequestTask requestTask, PmrvUser pmrvUser) {
		Request request = requestTask.getRequest();
		Long accountId = request.getAccountId();
        AviationAccountClosureSubmitRequestTaskPayload taskPayload =
            (AviationAccountClosureSubmitRequestTaskPayload) requestTask.getPayload();
        
        validatorService.validateAviationAccountClosureObject(taskPayload.getAviationAccountClosure());

        // update request payload
        AviationAccountClosureRequestPayload requestPayload = 
        		(AviationAccountClosureRequestPayload) request.getPayload();
        requestPayload.setAviationAccountClosure(taskPayload.getAviationAccountClosure());

        // add action
        addRequestAction(pmrvUser, request, taskPayload);	
        
        // update account status to CLOSED
        aviationAccountUpdateService.closeAviationAccount(
        		accountId, pmrvUser, taskPayload.getAviationAccountClosure().getReason());
	}

	private void addRequestAction(PmrvUser pmrvUser, Request request,
			AviationAccountClosureSubmitRequestTaskPayload taskPayload) {
		AviationAccountClosureSubmittedRequestActionPayload actionPayload = 
        		AviationAccountClosureSubmittedRequestActionPayload
        		.builder()
        		.payloadType(RequestActionPayloadType.AVIATION_ACCOUNT_CLOSURE_SUBMITTED_PAYLOAD)
        		.aviationAccountClosure(taskPayload.getAviationAccountClosure())
        		.build();
        		
        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.AVIATION_ACCOUNT_CLOSURE_SUBMITTED,
            pmrvUser.getUserId()
        );
	}
    
	@Transactional
    public void cancel(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = request.getPayload().getRegulatorAssignee();
        
        request.setStatus(RequestStatus.CANCELLED);
        requestService.addActionToRequest(request,
            null,
            RequestActionType.AVIATION_ACCOUNT_CLOSURE_CANCELLED,
            assignee);
    }
	
}
