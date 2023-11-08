package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

@Service
@RequiredArgsConstructor
public class PermitNotificationGrantedHandler implements JavaDelegate {

    private final PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        reviewSubmittedService.executeGrantedPostActions(requestId);
		execution.setVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_NEEDED,
				reviewSubmittedService.isFollowUpNeeded(requestId));
        
    }
}
