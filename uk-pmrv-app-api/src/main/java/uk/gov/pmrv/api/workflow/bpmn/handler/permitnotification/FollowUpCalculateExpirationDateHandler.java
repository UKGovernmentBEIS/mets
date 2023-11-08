package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowUpCalculateExpirationDateHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = reviewSubmittedService.resolveFollowUpExpirationDate(requestId);
        Map<String, Object> expirationVars = requestExpirationVarsBuilder
                .buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate);
        execution.setVariables(expirationVars);
    }
}