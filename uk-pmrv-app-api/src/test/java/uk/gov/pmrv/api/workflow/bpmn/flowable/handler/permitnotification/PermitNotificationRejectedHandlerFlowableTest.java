package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationRejectedHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitNotificationReviewSubmittedService service;

    @InjectMocks
    private PermitNotificationRejectedHandlerFlowable handler;

    @Test
    void execute_callsRejectedPostActionsWithRequestIdFromExecution() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        verify(service).executeRejectedPostActions(requestId);
        verifyNoMoreInteractions(service);
    }
}
