package uk.gov.pmrv.api.workflow.bpmn.camunda;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Import({CamundaBpmAutoConfiguration.class})
public class CamundaWorkflowService implements WorkflowService {
    private final RuntimeService runtimeService;

    private final TaskService taskService;

    /**
     * Starts a process definition in the workflow engine passing some paramaters.
     *
     * @param request the request
     * @param vars the variables to pass, can be null.
     * @return the process instance id
     */
    @Override
    public String startProcessDefinition(Request request, Map<String, Object> vars) {
        return startProcess(request, vars);
    }

    /**
     * ReStarts a process definition in the workflow engine passing some paramaters.
     *
     * @param request the request
     * @param vars the variables to pass, can be null.
     * @return the process instance id
     */
    @Override
    public String reStartProcessDefinition(Request request, Map<String, Object> vars) {
        return startProcess(request, vars);
    }

    @Override
    public void completeTask(String processTaskId) {
        completeTask(processTaskId, Map.of());
    }

    /**
     * Completes the task of the provided process task id.
     *
     * @param processTaskId the process task id
     * @param variables     task parameters. May be null or empty.
     */
    @Override
    public void completeTask(String processTaskId, Map<String, Object> variables) {
        taskService.complete(processTaskId, variables);
    }

    @Override
    public void sendEvent(final String requestId, final String message, final Map<String, Object> variables) {
        runtimeService.createMessageCorrelation(message)
            .processInstanceBusinessKey(WorkflowService.constructBusinessKey(requestId))
            .setVariables(variables)
            .correlateAll();
    }

    @Override
    public void sendEvent(String requestId, String message) {
        sendEvent(requestId, message, new HashMap<>());
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
            .messageEventSubscriptionName(messageName).count() > 0;
    }
    
    private String startProcess(Request request, Map<String, Object> vars) {
        final String businessKey = WorkflowService.constructBusinessKey((String) vars.get(BpmnProcessConstants.REQUEST_ID));
        vars.put(BpmnProcessConstants.BUSINESS_KEY, businessKey);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(request.getType().getProcessDefinitionId(), businessKey, vars);
        return instance.getProcessInstanceId();
    }
}
