package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class WasteQDRSecondReminderDateReachedHandler implements JavaDelegate {

    private final WasteQDRSendReminderNotificationService wasteQDRSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE);

        wasteQDRSendReminderNotificationService.sendSecondReminderNotification(requestId, expirationDate);
    }
}
