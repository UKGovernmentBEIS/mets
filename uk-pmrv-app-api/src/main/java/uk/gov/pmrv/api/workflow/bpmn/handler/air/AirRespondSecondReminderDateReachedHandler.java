package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirSendReminderNotificationService;

@Service
@RequiredArgsConstructor
public class AirRespondSecondReminderDateReachedHandler implements JavaDelegate {

    private final AirSendReminderNotificationService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE);

        service.sendRespondSecondReminderNotification(requestId, expirationDate);
    }
}
