package uk.gov.pmrv.api.workflow.bpmn.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2OfficialNoticeService;

@Service
@RequiredArgsConstructor
public class Bdrs2SendOfficialNoticeHandler implements JavaDelegate {

    private final BDRS2OfficialNoticeService officialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        officialNoticeService.sendOfficialNotice(requestId);
    }
}
