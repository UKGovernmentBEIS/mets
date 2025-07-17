package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationOperatorLedGrantedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitVariationGrantedGenerateDocumentsHandler implements JavaDelegate {

	private final PermitVariationOperatorLedGrantedGenerateDocumentsService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
	
}
