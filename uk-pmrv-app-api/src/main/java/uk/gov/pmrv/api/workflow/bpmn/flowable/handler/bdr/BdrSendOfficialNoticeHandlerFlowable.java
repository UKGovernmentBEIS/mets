package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDROfficialNoticeService;

@Service
@RequiredArgsConstructor
public class BdrSendOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final BDROfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService.sendOfficialNotice(requestId);
    }
}
