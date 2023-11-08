package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import java.util.Map;

public interface UserTaskCreatedHandler {
	
	void createRequestTask(String requestId, String processTaskId, String taskDefinitionKey,
			Map<String, Object> variables);

}
