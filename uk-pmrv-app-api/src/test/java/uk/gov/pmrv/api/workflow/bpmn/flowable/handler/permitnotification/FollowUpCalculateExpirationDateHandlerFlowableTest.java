package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class FollowUpCalculateExpirationDateHandlerFlowableTest {

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private FollowUpCalculateExpirationDateHandlerFlowable handler;

    @Test
    void execute_resolvesExpirationDate_buildsVars_andSetsThemOnExecution() {
        String requestId = "REQ-123";
        Date expirationDate = new Date();
        Map<String, Object> vars = Map.of(
                "followUpResponseExpirationDate", expirationDate,
                "someKey", "someValue"
        );

        Mockito.when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        Mockito.when(reviewSubmittedService.resolveFollowUpExpirationDate(requestId)).thenReturn(expirationDate);
        Mockito.when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate))
                .thenReturn(vars);

        handler.execute(execution);

        Mockito.verify(reviewSubmittedService).resolveFollowUpExpirationDate(requestId);
        Mockito.verify(requestExpirationVarsBuilder).buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(execution).setVariables(captor.capture());

        assertSame(vars, captor.getValue());

        Mockito.verifyNoMoreInteractions(reviewSubmittedService, requestExpirationVarsBuilder);
    }
}
