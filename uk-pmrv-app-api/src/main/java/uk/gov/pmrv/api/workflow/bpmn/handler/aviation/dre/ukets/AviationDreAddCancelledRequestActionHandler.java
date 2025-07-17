package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreAddCancelledRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDreAddCancelledRequestActionHandler implements JavaDelegate {

	private final AviationDreAddCancelledRequestActionService aviationDreAddCancelledRequestActionService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		aviationDreAddCancelledRequestActionService.add(requestId);
	}
}
