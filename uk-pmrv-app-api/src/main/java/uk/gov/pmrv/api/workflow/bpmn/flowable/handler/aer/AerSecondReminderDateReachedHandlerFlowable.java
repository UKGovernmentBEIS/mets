package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AerSecondReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final AerSendReminderNotificationService aerSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE);

        aerSendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
