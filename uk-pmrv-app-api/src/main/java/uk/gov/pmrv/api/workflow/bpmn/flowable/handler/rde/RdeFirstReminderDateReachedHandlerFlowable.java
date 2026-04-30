package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rde;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RdeFirstReminderDateReachedHandlerFlowable implements JavaDelegate {
    
    private final RdeSendReminderNotificationService rdeSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        rdeSendReminderNotificationService.sendFirstReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.RDE_EXPIRATION_DATE));
    }
}
