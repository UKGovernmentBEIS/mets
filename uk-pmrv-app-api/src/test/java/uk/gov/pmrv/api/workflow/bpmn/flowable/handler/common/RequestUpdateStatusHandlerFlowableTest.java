package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.common;

import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestUpdateStatusHandlerFlowableTest {

    @InjectMocks
    private RequestUpdateStatusHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestService requestService;

    @Test
    void execute() throws Exception {
        final String requestId = "1";
        final RequestStatus status = RequestStatus.APPROVED;
        handler.setRequestStatus(new FixedValue("APPROVED"));

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(requestService, times(1)).updateRequestStatus(requestId, status);
    }
}
