package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreAddCancelledRequestActionService;

@Service
@RequiredArgsConstructor
public class DreAddCancelledRequestActionHandlerFlowable implements JavaDelegate {

	private final DreAddCancelledRequestActionService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.add(requestId);
	}

}
