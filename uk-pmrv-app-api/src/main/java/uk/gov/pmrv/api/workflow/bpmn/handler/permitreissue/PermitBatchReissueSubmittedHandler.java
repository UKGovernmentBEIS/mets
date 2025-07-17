package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.BatchReissueSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitBatchReissueSubmittedHandler implements JavaDelegate {

	private final BatchReissueSubmittedService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.batchReissueSubmitted((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}

}
