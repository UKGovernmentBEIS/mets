package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
//https://www.flowable.com/open-source/docs/bpmn/ch03-Configuration#event-listener-implementation
public class ProcessInstanceEndListenerFlowable implements FlowableEventListener {
	
    private final RequestService requestService;

    @Override
    public void onEvent(FlowableEvent event) {
        ExecutionEntity execution = (ExecutionEntity)((FlowableEntityEvent) event).getEntity();
        if(execution.hasVariable(BpmnProcessConstants.REQUEST_ID)){
            String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
            Boolean shouldBeDeleted = (Boolean) execution.getVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE);
            requestService.terminateRequest(requestId, execution.getProcessInstanceId(), Boolean.TRUE.equals(shouldBeDeleted));
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
		return List.of(
				FlowableEngineEventType.PROCESS_COMPLETED,
				FlowableEngineEventType.PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT
				);
    }
}
