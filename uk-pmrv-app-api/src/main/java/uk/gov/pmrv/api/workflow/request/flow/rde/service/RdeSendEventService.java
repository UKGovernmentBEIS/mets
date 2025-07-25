package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RdeSendEventService {

    private final WorkflowService workflowService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    public void send(final String requestId, final LocalDate deadline) {
        final Map<String, Object> rdeVariables = new HashMap<>();
        rdeVariables.putAll(requestExpirationVarsBuilder
                .buildExpirationVars(RequestExpirationType.RDE, DateUtils.atEndOfDay(deadline)));
        
        workflowService.sendEvent(requestId, BpmnProcessConstants.RDE_REQUESTED, rdeVariables);
    }
}
