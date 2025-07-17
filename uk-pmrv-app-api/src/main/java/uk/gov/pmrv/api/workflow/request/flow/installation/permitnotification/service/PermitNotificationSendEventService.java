package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitNotificationSendEventService {

    private final WorkflowService workflowService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    public void extendTimer(final String requestId, final LocalDate dueDate) {
        final Map<String, Object> variables = requestExpirationVarsBuilder
                .buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, DateUtils.atEndOfDay(dueDate));

        workflowService.sendEvent(requestId, BpmnProcessConstants.FOLLOW_UP_TIMER_EXTENDED, variables);
    }
}
