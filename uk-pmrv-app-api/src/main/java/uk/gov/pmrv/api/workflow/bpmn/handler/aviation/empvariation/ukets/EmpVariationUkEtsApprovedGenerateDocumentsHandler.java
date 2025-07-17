package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApprovedGenerateDocumentsHandler implements JavaDelegate {

	private final EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
