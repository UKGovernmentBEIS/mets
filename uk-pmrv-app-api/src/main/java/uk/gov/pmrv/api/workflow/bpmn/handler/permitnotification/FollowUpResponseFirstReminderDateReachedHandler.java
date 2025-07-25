package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationFollowUpSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FollowUpResponseFirstReminderDateReachedHandler implements JavaDelegate {

    private final PermitNotificationFollowUpSendReminderNotificationService sendReminderNotificationService;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) delegateExecution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);

        sendReminderNotificationService.sendFirstReminderNotification(requestId, expirationDate);
    }
}
