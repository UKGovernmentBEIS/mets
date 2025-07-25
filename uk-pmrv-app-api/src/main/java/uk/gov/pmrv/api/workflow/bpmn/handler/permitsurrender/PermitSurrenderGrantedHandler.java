package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewGrantedService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PermitSurrenderGrantedHandler implements JavaDelegate {

    private final PermitSurrenderReviewGrantedService service;
    
    @Override
    public void execute(DelegateExecution execution) {
    	final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.executeGrantedPostActions(requestId);
        
        final Date noticeReminderDate = service.resolveNoticeReminderDate(requestId);
        execution.setVariable(BpmnProcessConstants.SURRENDER_REMINDER_NOTICE_DATE, noticeReminderDate);

        // Add variables for triggering AER
        service.constructAerVariables(requestId).forEach(execution::setVariable);
    }
}
