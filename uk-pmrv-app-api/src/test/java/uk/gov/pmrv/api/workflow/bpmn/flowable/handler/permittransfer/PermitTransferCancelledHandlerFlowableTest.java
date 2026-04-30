package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferCancelledService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferCancelledHandlerFlowableTest {

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private PermitTransferCancelledService service;

    @InjectMocks
    private PermitTransferCancelledHandlerFlowable handler;

    @Test
    void execute_callsCancelWithRequestIdFromExecution() {
        String requestId = "REQ-123";
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(delegateExecution);
        verify(service).cancel(requestId);
        verifyNoMoreInteractions(service);
    }
}
