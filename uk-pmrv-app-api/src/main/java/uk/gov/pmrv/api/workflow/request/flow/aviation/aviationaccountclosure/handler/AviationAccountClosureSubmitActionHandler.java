package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class AviationAccountClosureSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {
	
	private static final String TERMINATE_REASON = "Workflow terminated by the system because the account was closed";

	private final RequestService requestService;
    private final RequestAviationAccountClosureService requestAviationAccountClosureService;
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;
    private final RequestQueryService requestQueryService;
    

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        PmrvUser pmrvUser, RequestTaskActionEmptyPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Long accountId = requestTask.getRequest().getAccountId();

        requestAviationAccountClosureService.applySubmitAction(requestTask, pmrvUser);
        
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        // complete the current workflow
        workflowService.completeTask(requestTask.getProcessTaskId());
        
        // terminate all remaining workflows for this aviation account
        terminateAviationAccountWorkflows(accountId);
    }
    
    private void terminateAviationAccountWorkflows(Long accountId) {
    	List<Request> accountRequests = requestQueryService.findInProgressRequestsByAccount(accountId); 
    	
    	accountRequests.forEach(ar -> {
    		workflowService.deleteProcessInstance(ar.getProcessInstanceId(), TERMINATE_REASON);

            ar.setStatus(RequestStatus.CANCELLED);

            requestService.addActionToRequest(ar, null, RequestActionType.REQUEST_TERMINATED, null);
    	});  	
	}

	@Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_APPLICATION);
    }
}
