package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.vir;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class VirSendOfficialNoticeEmailHandlerFlowable implements JavaDelegate {

    private final VirOfficialNoticeService virOfficialNoticeService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);

        virOfficialNoticeService.sendOfficialNotice(requestId);
    }
}
