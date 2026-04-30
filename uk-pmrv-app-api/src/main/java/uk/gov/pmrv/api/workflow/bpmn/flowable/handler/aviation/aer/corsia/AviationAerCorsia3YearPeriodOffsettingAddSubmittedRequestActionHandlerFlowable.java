package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingAddSubmittedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingAddSubmittedRequestActionHandlerFlowable implements JavaDelegate {
    
    private final AviationAerCorsia3YearPeriodOffsettingAddSubmittedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.add(requestId);
    }
}
