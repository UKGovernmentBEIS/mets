package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaSubmitCancelActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        RequestTaskActionEmptyPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

		workflowService.completeTask(requestTask.getProcessTaskId(),
				Map.of(BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.CANCELLED));
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_CANCEL);
	}
}
