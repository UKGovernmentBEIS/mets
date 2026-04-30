package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.audit;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditAddSubmittedRequestActionService;

@Service
@RequiredArgsConstructor
public class InstallationAuditAddSubmittedRequestActionHandlerFlowable implements JavaDelegate {

    private final InstallationAuditAddSubmittedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.add(requestId);
    }
}
