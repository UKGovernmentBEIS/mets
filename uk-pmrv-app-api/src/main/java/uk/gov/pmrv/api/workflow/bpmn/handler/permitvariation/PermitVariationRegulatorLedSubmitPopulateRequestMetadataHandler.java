package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedSubmittedPopulateRequestMetadataService;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedSubmitPopulateRequestMetadataHandler implements JavaDelegate {
	
	private final PermitVariationRegulatorLedSubmittedPopulateRequestMetadataService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.populateRequestMetadata((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
