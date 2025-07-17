package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BdrFirstReminderDateReachedHandler implements JavaDelegate {

    private final BDRSendReminderNotificationService bdrSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.BDR_EXPIRATION_DATE);

        bdrSendReminderNotificationService.sendFirstReminderNotification(requestId, expirationDate);
    }
}
