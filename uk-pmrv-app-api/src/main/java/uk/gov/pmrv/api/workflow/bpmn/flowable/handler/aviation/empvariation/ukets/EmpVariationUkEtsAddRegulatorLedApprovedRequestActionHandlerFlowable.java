package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsAddRegulatorLedApprovedRequestActionHandlerFlowable implements JavaDelegate {

    private final EmpVariationUkEtsAddRegulatorLedApprovedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.add((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
