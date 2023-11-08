package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceGrantedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedGenerateDocumentsHandler implements JavaDelegate {

	private final PermitIssuanceGrantedGenerateDocumentsService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
	
}

