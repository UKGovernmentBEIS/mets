package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.BatchReissueCompletedService;

@Service
@RequiredArgsConstructor
public class EmpBatchReissueCompletedHandler implements JavaDelegate {

	private final BatchReissueCompletedService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.addAction((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
	
}
