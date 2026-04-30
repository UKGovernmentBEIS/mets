package uk.gov.pmrv.api.workflow.request;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.Map;

public interface WorkflowService {
    String BUSINESS_KEY_PREFIX = "bk";

    String startProcessDefinition(Request request, Map<String, Object> vars);

    String reStartProcessDefinition(Request request, Map<String, Object> vars);

    void completeTask(String processTaskId);

    void completeTask(String processTaskId, Map<String, Object> variables);

    void sendEvent(String requestId, String message, Map<String, Object> variables);
    void sendEvent(String requestId, String message);

    void deleteProcessInstance(String processInstanceId, String deleteReason);

    void setVariable(String processInstanceId, String variableName, Object value);

    boolean hasMessageEventSubscriptionWithName(final String requestId, final String messageName);

    static String constructBusinessKey(String requestId) {
        // a prefix is needed because camunda throws ClassCastException in case the business key looks like a number
        return BUSINESS_KEY_PREFIX + requestId;
    }
}
