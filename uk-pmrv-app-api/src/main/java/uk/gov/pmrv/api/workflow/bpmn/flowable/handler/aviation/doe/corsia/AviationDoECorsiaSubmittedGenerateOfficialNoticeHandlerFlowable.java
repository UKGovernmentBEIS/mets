package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.doe.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaOfficialNoticeGenerateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaSubmittedGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final AviationDoECorsiaOfficialNoticeGenerateService aviationDoECorsiaOfficialNoticeGenerateService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationDoECorsiaOfficialNoticeGenerateService.generateOfficialNotice(requestId);
    }
}
