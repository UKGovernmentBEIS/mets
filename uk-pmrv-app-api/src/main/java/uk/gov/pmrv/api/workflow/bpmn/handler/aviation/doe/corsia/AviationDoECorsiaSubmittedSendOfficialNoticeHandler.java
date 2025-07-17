package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.doe.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaOfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaSubmittedSendOfficialNoticeHandler implements JavaDelegate {

    private final AviationDoECorsiaOfficialNoticeSendService aviationDoECorsiaOfficialNoticeSendService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationDoECorsiaOfficialNoticeSendService.sendOfficialNotice(requestId);
    }
}
