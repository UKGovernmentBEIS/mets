package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class DefaultUserTaskCreatedHandler implements UserTaskCreatedHandler {
	
	protected final RequestTaskCreateService requestTaskCreateService;
	
    public void createRequestTask(final String requestId, final String processTaskId, final String taskDefinitionKey, final Map<String, Object> variables) {
    	final RequestTaskType requestTaskType;
    	final Optional<DynamicUserTaskDefinitionKey> dynamicKeyOpt = DynamicUserTaskDefinitionKey.fromString(taskDefinitionKey);
		
		if(dynamicKeyOpt.isPresent()) {
			requestTaskType = resolveDynamicRequestTaskType(taskDefinitionKey, variables);
    	} else {
    		requestTaskType = resolveFixedRequestTaskType(taskDefinitionKey);
    	}
    	
		if(requestTaskType.isExpirable()) {
			final Date dueDate = (Date) variables.get(requestTaskType.getExpirationKey().getCode() + BpmnProcessConstants._EXPIRATION_DATE);
            final LocalDate dueDateLd = dueDate != null ? dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(): null;
            requestTaskCreateService.create(requestId, processTaskId, requestTaskType, null, dueDateLd); 
        } else {
        	requestTaskCreateService.create(requestId, processTaskId, requestTaskType);
        }
    }
    
    private RequestTaskType resolveDynamicRequestTaskType(final String taskDefinitionKey, final Map<String, Object> variables) {
    	final String taskDefinitionKeyPrefix;
    	
    	// check if a specific prefix is defined in the bpmn variables
    	final String bpmnPrefixVar = (String)variables.get(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX);
    	if(bpmnPrefixVar != null) {
    		taskDefinitionKeyPrefix = bpmnPrefixVar;
    	} else {
        	// default case return the request type
    		taskDefinitionKeyPrefix = (String) variables.get(BpmnProcessConstants.REQUEST_TYPE);	
    	}
    	
        return RequestTaskType.valueOf(taskDefinitionKeyPrefix + "_" + taskDefinitionKey);
    }
    
    private RequestTaskType resolveFixedRequestTaskType(final String taskDefinitionKey) {
    	return RequestTaskType.valueOf(taskDefinitionKey);
    }
    
}
