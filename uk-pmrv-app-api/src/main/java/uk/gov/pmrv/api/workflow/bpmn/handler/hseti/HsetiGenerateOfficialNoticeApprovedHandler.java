package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class HsetiGenerateOfficialNoticeApprovedHandler implements JavaDelegate {

    private final HSETIOfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService.generateOfficialNoticeApproved(requestId);
    }
}
