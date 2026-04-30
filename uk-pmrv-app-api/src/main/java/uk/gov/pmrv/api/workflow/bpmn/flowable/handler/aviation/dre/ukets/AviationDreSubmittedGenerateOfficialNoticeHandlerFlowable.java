package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.dre.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreOfficialNoticeGenerateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDreSubmittedGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final AviationDreOfficialNoticeGenerateService aviationDreOfficialNoticeGenerateService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationDreOfficialNoticeGenerateService.generateOfficialNotice(requestId);
    }
}
