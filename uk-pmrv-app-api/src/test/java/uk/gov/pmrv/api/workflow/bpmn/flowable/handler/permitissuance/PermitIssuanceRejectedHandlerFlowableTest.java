package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitissuance;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceRejectedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedHandlerFlowableTest {

    @InjectMocks
    private PermitIssuanceRejectedHandlerFlowable handler;

    @Mock
    private PermitIssuanceRejectedService permitIssuanceRejectedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        //invoke
        handler.execute(execution);

        //verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(permitIssuanceRejectedService, times(1)).reject(requestId);
    }
}
