package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueGenerateReportService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpBatchReissueGenerateReportHandler implements JavaDelegate {

	private final EmpBatchReissueGenerateReportService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateReport((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}

}
