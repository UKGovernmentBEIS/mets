package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.CustomUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DefaultUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.UserTaskCreatedHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserTaskCreatedListenerFlowable implements FlowableEventListener {

	private final List<CustomUserTaskCreatedHandler> customUserTaskCreatedHandlers;
	private final DefaultUserTaskCreatedHandler defaultUserTaskCreatedHandler;

	@Override
	public void onEvent(FlowableEvent event) {
		TaskEntity taskEntity = (TaskEntity)((FlowableEntityEvent) event).getEntity();
		final String requestId = (String) taskEntity.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String processTaskId = taskEntity.getId();
		final String taskDefinitionKey = taskEntity.getTaskDefinitionKey();
		final Map<String, Object> variables = taskEntity.getVariables();

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

	@Override
	public boolean isFailOnException() {
		return true;
	}

	@Override
	public boolean isFireOnTransactionLifecycleEvent() {
		return false;
	}

	@Override
	public String getOnTransaction() {
		return null;
	}

	@Override
	public Collection<? extends FlowableEventType> getTypes() {
		return List.of(FlowableEngineEventType.TASK_CREATED);
	}
}
