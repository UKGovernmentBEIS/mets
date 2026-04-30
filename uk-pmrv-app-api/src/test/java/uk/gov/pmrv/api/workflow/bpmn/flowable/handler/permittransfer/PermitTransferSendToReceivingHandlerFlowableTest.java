package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBInitiateRequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferSendToReceivingHandlerFlowableTest {

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private PermitTransferBInitiateRequestService service;

    @InjectMocks
    private PermitTransferSendToReceivingHandlerFlowable handler;

    @Test
    void execute_callsInitiateAndSetsReceivingBusinessKey() {
        String transferARequestId = "REQ-A-1";
        String transferABusinessKey = "bk-A-1";
        String transferBBusinessKey = "bk-B-1";

        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(transferARequestId);
        when(delegateExecution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(transferABusinessKey);
        when(service.initiatePermitTransferBRequest(transferARequestId, transferABusinessKey))
                .thenReturn(transferBBusinessKey);

        handler.execute(delegateExecution);

        verify(delegateExecution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(delegateExecution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(service).initiatePermitTransferBRequest(transferARequestId, transferABusinessKey);
        verify(delegateExecution, times(1)).setVariable(BpmnProcessConstants.PERMIT_TRANSFER_RECEIVING_BUSINESS_KEY, transferBBusinessKey);
    }
}
