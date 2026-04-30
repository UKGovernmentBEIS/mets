package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingApplicationCancelledHandlerFlowable implements JavaDelegate {
    private final AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;


    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationAerCorsiaAnnualOffsettingSubmitService.cancel(requestId);
    }
}
