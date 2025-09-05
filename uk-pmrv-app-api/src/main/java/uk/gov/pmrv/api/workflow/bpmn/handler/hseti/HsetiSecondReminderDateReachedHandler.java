package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class HsetiSecondReminderDateReachedHandler implements JavaDelegate {

   private final HSETISendReminderNotificationService hsetiSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.HSE_TI_EXPIRATION_DATE);

        hsetiSendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }

}
