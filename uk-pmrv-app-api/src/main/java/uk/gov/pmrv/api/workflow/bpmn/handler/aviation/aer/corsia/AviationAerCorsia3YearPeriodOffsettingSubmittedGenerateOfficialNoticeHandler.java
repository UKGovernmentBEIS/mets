package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;


import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingSubmittedGenerateOfficialNoticeHandler implements JavaDelegate {
    
    private final AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService
                .generateAviationAerCorsia3YearPeriodOffsettingSubmittedOfficialNotice(requestId);
    }
}
