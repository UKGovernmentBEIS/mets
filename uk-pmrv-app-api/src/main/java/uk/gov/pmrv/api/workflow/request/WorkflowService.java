package uk.gov.pmrv.api.workflow.request;

import java.util.Map;

public interface WorkflowService {

    String startProcessDefinition(String processDefinitionId, Map<String, Object> vars);

    void completeTask(String processTaskId);

    void completeTask(String processTaskId, Map<String, Object> variables);

    void sendEvent(String requestId, String message, Map<String, Object> variables);

    void deleteProcessInstance(String processInstanceId, String deleteReason);

    void setVariable(String processInstanceId, String variableName, Object value);

    String constructBusinessKey(String requestId);

    boolean hasMessageEventSubscriptionWithName(final String requestId, final String messageName);
}
