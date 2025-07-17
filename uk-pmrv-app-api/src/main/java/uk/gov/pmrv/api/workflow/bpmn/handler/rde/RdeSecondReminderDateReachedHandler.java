package uk.gov.pmrv.api.workflow.bpmn.handler.rde;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RdeSecondReminderDateReachedHandler implements JavaDelegate {
    
    private final RdeSendReminderNotificationService rdeSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        rdeSendReminderNotificationService.sendSecondReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.RDE_EXPIRATION_DATE));
    }
}
