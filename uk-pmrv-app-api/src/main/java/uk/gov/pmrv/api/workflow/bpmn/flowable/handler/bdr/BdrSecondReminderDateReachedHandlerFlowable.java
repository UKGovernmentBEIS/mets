package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BdrSecondReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final BDRSendReminderNotificationService bdrSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.BDR_EXPIRATION_DATE);

        bdrSendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
