package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationCancelledService;

@Service
@RequiredArgsConstructor
public class PermitNotificationCancelledHandlerFlowable implements JavaDelegate {

    private final PermitNotificationCancelledService service;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        service.cancel((String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
