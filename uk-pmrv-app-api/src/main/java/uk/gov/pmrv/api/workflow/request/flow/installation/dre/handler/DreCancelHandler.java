package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSubmitOutcome;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DreCancelHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {
	
	private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			RequestTaskActionEmptyPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		
		workflowService.completeTask(requestTask.getProcessTaskId(),
				Map.of(BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.CANCELLED));
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.DRE_CANCEL_APPLICATION);
	}

}
