package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class HsetiSendOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final HSETIOfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService.sendOfficialNotice(requestId);
    }
}
