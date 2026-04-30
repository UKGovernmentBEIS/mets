package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaAddRegulatorLedApprovedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaAddRegulatorLedApprovedRequestActionHandlerFlowable implements JavaDelegate {

    private final EmpVariationCorsiaAddRegulatorLedApprovedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.add((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
