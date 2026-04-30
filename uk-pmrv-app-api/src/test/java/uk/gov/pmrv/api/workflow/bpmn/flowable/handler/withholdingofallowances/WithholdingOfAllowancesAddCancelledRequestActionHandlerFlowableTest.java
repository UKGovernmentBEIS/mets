package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.withholdingofallowances;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesApplicationCancelledService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesAddCancelledRequestActionHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private WithholdingOfAllowancesApplicationCancelledService service;

    @InjectMocks
    private WithholdingOfAllowancesAddCancelledRequestActionHandlerFlowable handler;

    @Test
    void execute_callsServiceCancel_withRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).cancel(requestId);
        verifyNoMoreInteractions(service);
    }
}
