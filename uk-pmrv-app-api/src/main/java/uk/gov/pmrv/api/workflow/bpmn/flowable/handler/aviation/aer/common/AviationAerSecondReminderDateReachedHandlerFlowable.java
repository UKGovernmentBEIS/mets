package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.common;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerSendReminderNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AviationAerSecondReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final AviationAerSendReminderNotificationService sendReminderNotificationService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        Date expirationDate = (Date) delegateExecution.getVariable(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE);

        sendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
