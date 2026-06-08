package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.ner;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NEROfficialNoticeService;

@Service
@RequiredArgsConstructor
public class NerSendOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final NEROfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService.sendOfficialNotice(requestId);
    }
}
