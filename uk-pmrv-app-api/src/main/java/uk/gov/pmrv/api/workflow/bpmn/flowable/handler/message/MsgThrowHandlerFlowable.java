package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.message;

import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.workflow.bpmn.flowable.FlowableWorkflowService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class MsgThrowHandlerFlowable implements JavaDelegate {
	
	@Setter
	private FixedValue messageName;

	private final FlowableWorkflowService flowableWorkflowService;

	@Override
	public void execute(DelegateExecution execution) {
		final String messageNameStr = (String) messageName.getValue(execution);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		
        flowableWorkflowService.sendEvent(requestId, messageNameStr);
	}

}
