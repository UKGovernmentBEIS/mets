package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitBatchReissueGenerateReportService;

@Service
@RequiredArgsConstructor
public class PermitBatchReissueGenerateReportHandlerFlowable implements JavaDelegate {

	private final PermitBatchReissueGenerateReportService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		service.generateReport((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}

}
