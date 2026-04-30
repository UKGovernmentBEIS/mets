package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.ukets;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceUkEtsApprovedService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApprovedHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private EmpIssuanceUkEtsApprovedService empIssuanceUkEtsApprovedService;

    @InjectMocks
    private EmpIssuanceUkEtsApprovedHandlerFlowable handler;

    @Test
    void execute_callsServiceApproveEmp_withRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(empIssuanceUkEtsApprovedService).approveEmp(requestId);
        verifyNoMoreInteractions(empIssuanceUkEtsApprovedService);
    }
}
