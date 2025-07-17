package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class DoalSendOfficialNoticeEmailHandler implements JavaDelegate {

    private final DoalOfficialNoticeService doalOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        doalOfficialNoticeService.sendOfficialNotice(requestId);
    }
}
