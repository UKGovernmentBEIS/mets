package uk.gov.pmrv.api.workflow.bpmn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.bpmn.camunda.CamundaWorkflowService;
import uk.gov.pmrv.api.workflow.bpmn.flowable.FlowableWorkflowService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class WorkflowTypeServiceDelegator implements WorkflowService {
	
    private final FlowableWorkflowService flowableWorkflowService;
    private final CamundaWorkflowService camundaWorkflowService;
    private final WorkflowTypeProvider requestTypeProvider;
    private final WorkflowTypeServiceProperties workflowTypeServiceProperties;
    
    @Override
    public String startProcessDefinition(Request request, Map<String, Object> vars) {
        return getWorkflowServiceByType(request.getType().name()).startProcessDefinition(request, vars);
    }

    @Override
    public String reStartProcessDefinition(Request request, Map<String, Object> vars) {
        return getWorkflowServiceByEngine(request.getEngine()).reStartProcessDefinition(request, vars);
    }

    @Override
    public void completeTask(String processTaskId) {
        completeTask(processTaskId, Map.of());
    }

    @Override
    public void completeTask(String processTaskId, Map<String, Object> variables) {
    	WorkflowEngineType engine = requestTypeProvider.findWorkflowEngineByProcessTaskId(processTaskId);
    	getWorkflowServiceByEngine(engine).completeTask(processTaskId, variables);
    }

    @Override
    public void sendEvent(final String requestId, final String message, final Map<String, Object> variables) {
    	WorkflowEngineType engine = requestTypeProvider.findWorkflowEngineByRequestId(requestId);
    	getWorkflowServiceByEngine(engine).sendEvent(requestId, message, variables);
    }

    @Override
    public void sendEvent(String requestId, String message) {
        sendEvent(requestId, message, new HashMap<>());
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason) {
    	WorkflowEngineType engine = requestTypeProvider.findWorkflowEngineByProcessInstanceId(processInstanceId);
        getWorkflowServiceByEngine(engine).deleteProcessInstance(processInstanceId, deleteReason);
    }

    @Override
    public void setVariable(String processInstanceId, String variableName, Object value) {
    	WorkflowEngineType engine = requestTypeProvider.findWorkflowEngineByProcessInstanceId(processInstanceId);
    	getWorkflowServiceByEngine(engine).setVariable(processInstanceId, variableName, value);
    }
    
    @Override
    public boolean hasMessageEventSubscriptionWithName(String requestId, String messageName) {
    	WorkflowEngineType engine = requestTypeProvider.findWorkflowEngineByRequestId(requestId);
        return getWorkflowServiceByEngine(engine).hasMessageEventSubscriptionWithName(requestId, messageName);
    }

    public WorkflowEngineType getWorkflowEngineByType(String workflowType) {
		return workflowTypeServiceProperties.getFlowableWorkflows().contains(workflowType) ? WorkflowEngineType.FLOWABLE
				: WorkflowEngineType.CAMUNDA;
	}

	private WorkflowService getWorkflowServiceByType(String workflowType) {
		return getWorkflowServiceByEngine(getWorkflowEngineByType(workflowType));
	}
	
	private WorkflowService getWorkflowServiceByEngine(WorkflowEngineType workflowEngine) {
		if (workflowEngine == null) {
			throw new IllegalArgumentException("Workflow's engine cannot be null");
		}

		return switch (workflowEngine) {
		case CAMUNDA -> camundaWorkflowService;
		case FLOWABLE -> flowableWorkflowService;
		default -> throw new IllegalStateException("Unexpected value: " + workflowEngine);
		};
	}
}
