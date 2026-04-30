package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsCompleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsCompleteHandlerFlowable implements JavaDelegate {

    private final AviationAerUkEtsCompleteService aviationAerUkEtsCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationAerUkEtsCompleteService.complete(requestId);
    }
}
