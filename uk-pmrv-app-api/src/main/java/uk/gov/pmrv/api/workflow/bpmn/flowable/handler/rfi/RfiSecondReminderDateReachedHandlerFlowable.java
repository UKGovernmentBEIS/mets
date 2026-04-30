package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rfi;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSendReminderNotificationService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RfiSecondReminderDateReachedHandlerFlowable implements JavaDelegate {
    
    private final RfiSendReminderNotificationService rfiSendReminderNotificationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        rfiSendReminderNotificationService.sendSecondReminderNotification(
                (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID),
                (Date) delegateExecution.getVariable(BpmnProcessConstants.RFI_EXPIRATION_DATE));
    }
}
