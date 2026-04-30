package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationAddCancelledRequestActionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitVariationAddCancelledRequestActionHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitVariationAddCancelledRequestActionService service;

    @InjectMocks
    private PermitVariationAddCancelledRequestActionHandlerFlowable handler;

    @Test
    void execute_callsServiceAdd_withRequestIdAndUserRole() {
        String requestId = "REQ-1";
        String userRole = "OPERATOR";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE)).thenReturn(userRole);

        handler.execute(execution);

        verify(service).add(requestId, userRole);
        verifyNoMoreInteractions(service);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
    }
}
