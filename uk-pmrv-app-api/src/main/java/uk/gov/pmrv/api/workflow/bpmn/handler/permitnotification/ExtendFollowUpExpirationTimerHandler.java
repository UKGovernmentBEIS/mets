package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.ExtendFollowUpExpirationTimerService;

@Service
@RequiredArgsConstructor
public class ExtendFollowUpExpirationTimerHandler implements JavaDelegate {

    private final ExtendFollowUpExpirationTimerService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);
        service.extendTimer(requestId, expirationDate);
    }
}
