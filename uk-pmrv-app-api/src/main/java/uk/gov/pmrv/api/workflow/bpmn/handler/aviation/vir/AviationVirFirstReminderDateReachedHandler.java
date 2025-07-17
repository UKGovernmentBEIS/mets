package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.vir;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AviationVirFirstReminderDateReachedHandler implements JavaDelegate {

    private final VirSendReminderNotificationService virSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AVIATION_VIR_EXPIRATION_DATE);

        virSendReminderNotificationService.sendFirstReminderNotification(requestId, expirationDate);
    }
}
