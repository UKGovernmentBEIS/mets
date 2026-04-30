package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandlerResolver;
import uk.gov.pmrv.api.workflow.request.application.taskdeleted.RequestTaskDeleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserTaskDeletedListenerFlowable implements FlowableEventListener {
	
    private final DynamicUserTaskDeletedHandlerResolver dynamicUserTaskDeletedHandlerMapper;
    private final RequestTaskDeleteService requestTaskDeleteService;

    @Override
    public void onEvent(FlowableEvent event) {
        if (((FlowableEntityEvent) event).getEntity() instanceof TaskEntity taskEntity) {
            final String taskDefinitionKey = taskEntity.getTaskDefinitionKey();
            final String processTaskId = taskEntity.getId();
            final Optional<DynamicUserTaskDeletedHandler> handler = dynamicUserTaskDeletedHandlerMapper.get(taskDefinitionKey);
            if (handler.isPresent()) {
                final String requestId = (String) taskEntity.getVariable(BpmnProcessConstants.REQUEST_ID);
                final Map<String, Object> variables = taskEntity.getVariables();
                handler.get().process(requestId, variables);
            }
            requestTaskDeleteService.delete(processTaskId);
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
        return List.of(FlowableEngineEventType.ENTITY_DELETED);
    }

    }
