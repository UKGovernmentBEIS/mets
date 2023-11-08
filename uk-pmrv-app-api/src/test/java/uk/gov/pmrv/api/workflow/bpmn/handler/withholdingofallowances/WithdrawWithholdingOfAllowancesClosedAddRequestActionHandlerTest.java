package uk.gov.pmrv.api.workflow.bpmn.handler.withholdingofallowances;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesClosedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawWithholdingOfAllowancesClosedAddRequestActionHandlerTest {

    @Mock
    private WithholdingOfAllowancesClosedService service;

    @InjectMocks
    private WithdrawWithholdingOfAllowancesClosedAddRequestActionHandler actionHandler;

    @Test
    void execute() {
        String requestId = "request123";
        DelegateExecution execution = Mockito.mock(DelegateExecution.class);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        actionHandler.execute(execution);

        verify(service).close(requestId);
    }

}