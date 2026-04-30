package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedApprovedGenerateDocumentsService;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedApprovedGenerateDocumentsHandlerFlowable implements JavaDelegate {

	private final PermitVariationRegulatorLedApprovedGenerateDocumentsService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
