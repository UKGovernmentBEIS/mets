package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewGrantedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderGrantedHandlerTest {

    @InjectMocks
    private PermitSurrenderGrantedHandler handler;

    @Mock
    private PermitSurrenderReviewGrantedService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";
        final Date noticeReminderDate = new Date();
        final Date reportDate = new Date();
        Map<String, Object> variables = new HashMap<>(){{
            put(BpmnProcessConstants.AER_REQUIRED, true);
            put(BpmnProcessConstants.AER_EXPIRATION_DATE, reportDate);
        }};

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(service.resolveNoticeReminderDate(requestId)).thenReturn(noticeReminderDate);
        when(service.constructAerVariables(requestId)).thenReturn(variables);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).executeGrantedPostActions(requestId);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.SURRENDER_REMINDER_NOTICE_DATE, noticeReminderDate);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.AER_REQUIRED, true);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.AER_EXPIRATION_DATE, reportDate);
    }
}
