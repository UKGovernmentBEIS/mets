package uk.gov.pmrv.api.workflow.bpmn;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

/**
 * Service for communicating with the BPMN Process Engine.
 */
@Service
@RequiredArgsConstructor
@Import({CamundaBpmAutoConfiguration.class})
public class CamundaWorkflowService implements WorkflowService {

    private static final String BUSINESS_KEY_PREFIX = "bk";

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    /**
     * Starts a process definition in the workflow engine passing some parmaters.
     *
     * @param processDefinitionId the process definition id
     * @param vars                the variables to pass, can be null.
     * @return the process instance id
     */
    @Override
    public String startProcessDefinition(String processDefinitionId, Map<String, Object> vars) {

        final String businessKey = this.constructBusinessKey((String) vars.get(BpmnProcessConstants.REQUEST_ID));
        vars.put(BpmnProcessConstants.BUSINESS_KEY, businessKey);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionId, businessKey, vars);
        return instance.getProcessInstanceId();
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
            .processInstanceBusinessKey(this.constructBusinessKey(requestId))
            .setVariables(variables)
            .correlateAll();
    }

    @Override
    public boolean hasMessageEventSubscriptionWithName(final String requestId, final String messageName) {
        return runtimeService
            .createExecutionQuery()
            .processInstanceBusinessKey(this.constructBusinessKey(requestId))
            .messageEventSubscriptionName(messageName).count() > 0;
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
    public String constructBusinessKey(final String requestId) {
        // a prefix is needed because camunda throws ClassCastException in case the business key looks like a number
        return BUSINESS_KEY_PREFIX + requestId;
    }
}
