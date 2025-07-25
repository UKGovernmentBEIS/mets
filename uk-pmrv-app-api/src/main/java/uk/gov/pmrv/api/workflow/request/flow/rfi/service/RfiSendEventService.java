package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RfiSendEventService {

    private final WorkflowService workflowService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    public void send(final String requestId, final LocalDate deadline) {
        final Map<String, Object> rfiVariables = new HashMap<>();
        rfiVariables.put(BpmnProcessConstants.RFI_START_TIME, new Date());
        rfiVariables.putAll(requestExpirationVarsBuilder
                .buildExpirationVars(RequestExpirationType.RFI, DateUtils.atEndOfDay(deadline)));
        
        workflowService.sendEvent(requestId, BpmnProcessConstants.RFI_REQUESTED, rfiVariables);
    }
}
