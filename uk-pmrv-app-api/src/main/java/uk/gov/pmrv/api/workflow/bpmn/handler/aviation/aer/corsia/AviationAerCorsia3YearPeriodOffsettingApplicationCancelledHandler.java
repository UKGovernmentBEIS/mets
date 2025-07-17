package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingApplicationCancelledHandler implements JavaDelegate {

    private final AviationAerCorsia3YearPeriodOffsettingSubmitService aviationAerCorsia3YearPeriodOffsettingSubmitService;


    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationAerCorsia3YearPeriodOffsettingSubmitService.cancel(requestId);
    }
}
