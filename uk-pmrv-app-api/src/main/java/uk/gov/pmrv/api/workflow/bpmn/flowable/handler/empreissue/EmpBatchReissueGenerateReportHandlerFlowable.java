package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueGenerateReportService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpBatchReissueGenerateReportHandlerFlowable implements JavaDelegate {

	private final EmpBatchReissueGenerateReportService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateReport((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
