package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.dre.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreUkEtsAddSubmittedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDreAddSubmittedRequestActionHandlerFlowable implements JavaDelegate {

	private final AviationDreUkEtsAddSubmittedRequestActionService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.add(requestId);
	}
}
