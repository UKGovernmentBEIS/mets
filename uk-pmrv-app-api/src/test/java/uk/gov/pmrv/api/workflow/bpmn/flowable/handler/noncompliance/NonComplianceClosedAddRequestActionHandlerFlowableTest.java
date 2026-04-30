package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.noncompliance;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceClosedAddRequestActionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceClosedAddRequestActionHandlerFlowableTest {

    @Mock
    private NonComplianceClosedAddRequestActionService service;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private NonComplianceClosedAddRequestActionHandlerFlowable handler;

    @Test
    void execute_callsAddRequestAction_withRequestIdFromExecution() {
        String requestId = "REQ-001";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).addRequestAction(requestId);
        verifyNoMoreInteractions(service);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_callsAddRequestAction_withNull_whenRequestIdMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);

        handler.execute(execution);

        verify(service).addRequestAction(null);
        verifyNoMoreInteractions(service);
    }
}
