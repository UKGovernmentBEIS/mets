package uk.gov.pmrv.api.workflow.bpmn.flowable;

import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableWorkflowService implements WorkflowService, InitializingBean {

    private final RuntimeService runtimeService;

    private final TaskService taskService;
    private final List<FlowableEventListener> eventListeners;

    @Override
    public String startProcessDefinition(Request request, Map<String, Object> vars) {
        return startProcess(request, vars);
    }

    @Override
    public String reStartProcessDefinition(Request request, Map<String, Object> vars) {
        return startProcess(request, vars);
    }

    @Override
    public void completeTask(String processTaskId) {
        completeTask(processTaskId, Map.of());
    }

    @Override
    public void completeTask(String processTaskId, Map<String, Object> variables) {
        taskService.complete(processTaskId, variables);
    }

    @Override
    public void sendEvent(String requestId, String messageName, Map<String, Object> variables) {
        runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageName)
                .processInstanceBusinessKey(WorkflowService.constructBusinessKey(requestId), true)
                .list()
                .forEach(execution -> runtimeService.messageEventReceived(messageName, execution.getId(), variables));
    }

    @Override
    public void sendEvent(String requestId, String message) {
        sendEvent(requestId, message, new HashMap<>());
    }
    
    public String getProcessInstanceIdByBusinessKey(String businessKey) {
		return runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey)
				.singleResult()
				.getProcessInstanceId();
	}

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }

    @Override
    public void setVariable(String processInstanceId, String variableName, Object value) {
        runtimeService.setVariable(processInstanceId, variableName, value);
    }
    
    @Override
    public boolean hasMessageEventSubscriptionWithName(final String requestId, final String messageName) {
        return runtimeService
            .createExecutionQuery()
            .processInstanceBusinessKey(WorkflowService.constructBusinessKey(requestId))
            .list()
            .stream()
            .findFirst()
            .map(execution -> runtimeService.createEventSubscriptionQuery()
                .processInstanceId(execution.getProcessInstanceId())
                .eventType("message")
                .eventName(messageName)
                .count() > 0)
            .orElse(false);
    }

    @Override
    public void afterPropertiesSet() {
        eventListeners.forEach(runtimeService::addEventListener);
    }
    
    private String startProcess(Request request, Map<String, Object> vars) {
        final String businessKey = WorkflowService.constructBusinessKey((String) vars.get(BpmnProcessConstants.REQUEST_ID));
        vars.put(BpmnProcessConstants.BUSINESS_KEY, businessKey);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(request.getType().getProcessDefinitionId(), businessKey, vars);
        return instance.getProcessInstanceId();
    }
}
