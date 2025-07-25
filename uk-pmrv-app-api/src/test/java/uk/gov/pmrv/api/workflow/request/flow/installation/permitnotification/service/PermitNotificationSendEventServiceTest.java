package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationSendEventServiceTest {

    @InjectMocks
    private PermitNotificationSendEventService service;

    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Test
    void extendTimer() {

        final String requestId = "1";
        final LocalDate dueDate = LocalDate.of(2023, 1, 1);
        final Date expirationDate = DateUtils.atEndOfDay(dueDate);
        Map<String, Object> vars = Map.of(
                "var1", "val1"
                );

        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate)).thenReturn(vars);

        // Invoke
        service.extendTimer(requestId, dueDate);

        // Verify
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate);
        verify(workflowService, times(1)).sendEvent(requestId, BpmnProcessConstants.FOLLOW_UP_TIMER_EXTENDED, vars);
    }
}
