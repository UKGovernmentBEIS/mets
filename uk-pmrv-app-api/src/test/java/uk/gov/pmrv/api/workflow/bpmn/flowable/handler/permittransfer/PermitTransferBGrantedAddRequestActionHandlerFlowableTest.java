package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBGrantedAddRequestActionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBGrantedAddRequestActionHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitTransferBGrantedAddRequestActionService service;

    @InjectMocks
    private PermitTransferBGrantedAddRequestActionHandlerFlowable handler;

    @Test
    void execute_callsAddRequestActionWithRequestIdFromExecution() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(execution);
        verify(service).addRequestAction(requestId);
        verifyNoMoreInteractions(service);
    }
}
