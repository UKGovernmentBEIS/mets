package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.application.taskcompleted.RequestTaskCompleteService;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserTaskCompletedListenerFlowable implements FlowableEventListener {
	
	private final RequestTaskCompleteService requestTaskCompleteService;

	@Override
	public void onEvent(FlowableEvent event) {
		TaskEntity taskEntity = (TaskEntity)((FlowableEntityEvent) event).getEntity();
		requestTaskCompleteService.complete(taskEntity.getId());
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
		return List.of(FlowableEngineEventType.TASK_COMPLETED);
	}
}
