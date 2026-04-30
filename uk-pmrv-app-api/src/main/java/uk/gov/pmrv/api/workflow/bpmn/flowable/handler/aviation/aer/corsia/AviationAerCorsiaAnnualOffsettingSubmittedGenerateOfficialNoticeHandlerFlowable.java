package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingSubmittedGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final AviationAerCorsiaAnnualOffsettingOfficialNoticeService
            aviationAerCorsiaAnnualOffsettingOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationAerCorsiaAnnualOffsettingOfficialNoticeService
                .generateAviationAerCorsiaAnnualOffsettingSubmittedOfficialNotice(requestId);
    }
}
