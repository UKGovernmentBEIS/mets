package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsRegulatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsRegulatorLedApprovedGenerateDocumentsHandler implements JavaDelegate {

	private final EmpVariationUkEtsRegulatorLedApprovedGenerateDocumentsService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
	
}