package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class VirSendOfficialNoticeEmailHandler implements JavaDelegate {

    private final VirOfficialNoticeService virOfficialNoticeService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);

        virOfficialNoticeService.sendOfficialNotice(requestId);
    }
}
