package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceSendOfficialNoticeEmailHandlerFlowable implements JavaDelegate {

    private final PermitIssuanceOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {
        
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.sendOfficialNotice(requestId);
    }
}
