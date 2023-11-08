package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CalculateApplicationReviewExpirationDateService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.CalculatePermitNotificationReviewExpirationDateService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateApplicationReviewExpirationDateHandlerTest {

    @InjectMocks
    private CalculateApplicationReviewExpirationDateHandler handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private CalculatePermitNotificationReviewExpirationDateService calculatePermitNotificationReviewExpirationDateService;

    @Spy
    private ArrayList<CalculateApplicationReviewExpirationDateService> expirationDateServices;

    @BeforeEach
    void setUp() {
        expirationDateServices.add(calculatePermitNotificationReviewExpirationDateService);
    }
    
    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final RequestType requestType = RequestType.PERMIT_NOTIFICATION;
        final Map<String, Object> vars = Map.of("var1", "val1");
        final Date date = new Date();
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_TYPE)).thenReturn(requestType.name());
        when(calculatePermitNotificationReviewExpirationDateService.getTypes()).thenReturn(Set.of(RequestType.PERMIT_NOTIFICATION));
        when(calculatePermitNotificationReviewExpirationDateService.expirationDate()).thenReturn(Optional.of(date));
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW, date))
            .thenReturn(vars);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW, date);
        verify(execution, times(1)).setVariables(vars);
        verify(calculatePermitNotificationReviewExpirationDateService, times(1)).getTypes();
        verify(calculatePermitNotificationReviewExpirationDateService, times(1)).expirationDate();
        verifyNoMoreInteractions(requestExpirationVarsBuilder);
    }

    @Test
    void execute_no_expiration_date() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final RequestType requestType = RequestType.PERMIT_NOTIFICATION;
        final Map<String, Object> vars = Map.of("var1", "val1");

        when(execution.getVariable(BpmnProcessConstants.REQUEST_TYPE)).thenReturn(requestType.name());
        when(calculatePermitNotificationReviewExpirationDateService.getTypes()).thenReturn(Set.of(RequestType.PERMIT_NOTIFICATION));
        when(calculatePermitNotificationReviewExpirationDateService.expirationDate()).thenReturn(Optional.empty());

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, never()).setVariables(vars);
        verify(calculatePermitNotificationReviewExpirationDateService, times(1)).getTypes();
        verify(calculatePermitNotificationReviewExpirationDateService, times(1)).expirationDate();
        verifyNoInteractions(requestExpirationVarsBuilder);
    }

    @Test
    void execute_default_expiration_date() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final RequestType requestType = RequestType.PERMIT_VARIATION;
        final Map<String, Object> vars = Map.of("var1", "val1");

        when(execution.getVariable(BpmnProcessConstants.REQUEST_TYPE)).thenReturn(requestType.name());
        when(calculatePermitNotificationReviewExpirationDateService.getTypes()).thenReturn(Set.of(RequestType.PERMIT_NOTIFICATION));
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW)).thenReturn(vars);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW);
        verify(execution, times(1)).setVariables(vars);
        verify(calculatePermitNotificationReviewExpirationDateService, times(1)).getTypes();
        verifyNoMoreInteractions(requestExpirationVarsBuilder);
    }
}
