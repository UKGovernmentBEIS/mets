package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AirRespondFirstReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final AirSendReminderNotificationService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE);

        service.sendRespondFirstReminderNotification(requestId, expirationDate);
    }
}
