package uk.gov.pmrv.api.workflow.bpmn.handler.permittransfer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBInitiateRequestService;

@ExtendWith(MockitoExtension.class)
class PermitTransferSendToReceivingHandlerTest {

    @InjectMocks
    private PermitTransferSendToReceivingHandler handler;

    @Mock
    private PermitTransferBInitiateRequestService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {

        final String transferARequestId = "1";
        final String transferABusinessKey = "bk-A-1";
        final String transferBBusinessKey = "bk-B-1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(transferARequestId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(transferABusinessKey);
        when(service.initiatePermitTransferBRequest(transferARequestId, transferABusinessKey))
            .thenReturn(transferBBusinessKey);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.PERMIT_TRANSFER_RECEIVING_BUSINESS_KEY, transferBBusinessKey);
    }
}
