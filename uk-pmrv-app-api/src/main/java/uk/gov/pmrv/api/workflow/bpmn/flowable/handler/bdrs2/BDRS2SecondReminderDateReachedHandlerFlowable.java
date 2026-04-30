package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2SendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BDRS2SecondReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final BDRS2SendReminderNotificationService bdrs2SendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.BDRS2_EXPIRATION_DATE);
        bdrs2SendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
