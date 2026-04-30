package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ApplicationReviewSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ApplicationReviewSecondReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final ApplicationReviewSendReminderNotificationService sendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        sendReminderNotificationService.sendSecondReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE));
    }
}
