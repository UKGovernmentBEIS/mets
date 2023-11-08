package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.ukets;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsAddRegulatorLedApprovedRequestActionHandler implements JavaDelegate {
	
	private final EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        service.add((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
	
}