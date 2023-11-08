package uk.gov.pmrv.api.workflow.bpmn.listener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.CustomUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DefaultUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.UserTaskCreatedHandler;

/**
 * Camunda listener that listens to creation of a user task 
 * 
 */
@Component
@RequiredArgsConstructor
public class UserTaskCreatedListener {

	private final List<CustomUserTaskCreatedHandler> customUserTaskCreatedHandlers;
	private final DefaultUserTaskCreatedHandler defaultUserTaskCreatedHandler;
	
	@Transactional
	@EventListener(condition = "#taskDelegate.eventName=='create'")
	public void onTaskCreatedEvent(DelegateTask taskDelegate) {
		final String requestId = (String) taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String processTaskId = taskDelegate.getId();
		final String taskDefinitionKey = taskDelegate.getTaskDefinitionKey();
		final Map<String, Object> variables = taskDelegate.getVariables();
		
		resolveHandler(taskDefinitionKey).createRequestTask(requestId, processTaskId, taskDefinitionKey, variables);
	}

	private UserTaskCreatedHandler resolveHandler(final String taskDefinitionKey) {
		Optional<CustomUserTaskCreatedHandler> customHandlerOpt = customUserTaskCreatedHandlers.stream()
				.filter(handler -> taskDefinitionKey.equals(handler.getTaskDefinitionKey()))
				.findFirst();
		
		if(customHandlerOpt.isPresent()) {
			return customHandlerOpt.get();
		} else {
			return defaultUserTaskCreatedHandler;
		}
	}
}
