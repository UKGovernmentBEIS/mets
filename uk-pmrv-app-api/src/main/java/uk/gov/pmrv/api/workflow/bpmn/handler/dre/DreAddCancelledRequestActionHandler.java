package uk.gov.pmrv.api.workflow.bpmn.handler.dre;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreAddCancelledRequestActionService;

@Service
@RequiredArgsConstructor
public class DreAddCancelledRequestActionHandler implements JavaDelegate {

	private final DreAddCancelledRequestActionService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.add(requestId);
	}

}
