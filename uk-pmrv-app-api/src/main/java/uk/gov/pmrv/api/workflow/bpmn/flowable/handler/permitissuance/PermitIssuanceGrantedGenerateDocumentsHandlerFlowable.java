package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceGrantedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedGenerateDocumentsHandlerFlowable implements JavaDelegate {

	private final PermitIssuanceGrantedGenerateDocumentsService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
	
}

