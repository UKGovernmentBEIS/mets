package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.CustomUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SystemMessageNotificationCreatedHandler implements CustomUserTaskCreatedHandler {
	
	protected final RequestTaskCreateService requestTaskCreateService;
	
	@Override
	public String getTaskDefinitionKey() {
		return DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name();
	}

    @Override
    public void createRequestTask(final String requestId, final String processTaskId, final String taskDefinitionKey, final Map<String, Object> variables) {
        final String taskType = variables.get(BpmnProcessConstants.REQUEST_TASK_TYPE).toString();
        final String requestTaskAssignee = variables.get(BpmnProcessConstants.REQUEST_TASK_ASSIGNEE).toString();
        final RequestTaskType requestTaskType = RequestTaskType.valueOf(taskType);
        
        requestTaskCreateService.create(requestId, processTaskId, requestTaskType, requestTaskAssignee);
    }

    
}
