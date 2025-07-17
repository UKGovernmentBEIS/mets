package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationUpdatePermitRegulatorLedService;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedUpdatePermitUponSubmitHandler implements JavaDelegate {

	private final PermitVariationUpdatePermitRegulatorLedService service;

    @Override
    public void execute(DelegateExecution execution) {
    	service.updatePermitUponSubmit((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
