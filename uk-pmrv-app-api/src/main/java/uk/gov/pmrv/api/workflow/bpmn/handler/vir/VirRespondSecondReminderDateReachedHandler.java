package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class VirRespondSecondReminderDateReachedHandler implements JavaDelegate {

    private final VirSendReminderNotificationService virSendReminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.VIR_EXPIRATION_DATE);

        virSendReminderNotificationService.sendRespondSecondReminderNotification(requestId, expirationDate);
    }
}
