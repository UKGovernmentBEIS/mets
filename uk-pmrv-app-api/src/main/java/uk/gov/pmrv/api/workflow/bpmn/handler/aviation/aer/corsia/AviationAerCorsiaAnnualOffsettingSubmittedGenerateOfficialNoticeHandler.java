package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingSubmittedGenerateOfficialNoticeHandler implements JavaDelegate {

    private final AviationAerCorsiaAnnualOffsettingOfficialNoticeService
            aviationAerCorsiaAnnualOffsettingOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationAerCorsiaAnnualOffsettingOfficialNoticeService
                .generateAviationAerCorsiaAnnualOffsettingSubmittedOfficialNotice(requestId);
    }
}
