package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitBatchReissueGenerateReportService;

@Service
@RequiredArgsConstructor
public class PermitBatchReissueGenerateReportHandler implements JavaDelegate {

	private final PermitBatchReissueGenerateReportService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateReport((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}

}
