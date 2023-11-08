package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerSendReminderNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AviationAerSecondReminderDateReachedHandler implements JavaDelegate {

    private final AviationAerSendReminderNotificationService sendReminderNotificationService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        Date expirationDate = (Date) delegateExecution.getVariable(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE);

        sendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
