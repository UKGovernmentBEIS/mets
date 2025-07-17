package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawalService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowanceWithdrawalCloseActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;
    @Mock
    private WithholdingOfAllowancesWithdrawalService withholdingOfAllowancesWithdrawalService;
    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WithholdingOfAllowanceWithdrawalCloseActionHandler handler;

    @Test
    void process() {
        Long requestTaskId = 123L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION;
        AppUser appUser = mock(AppUser.class);
        WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload taskActionPayload = mock(WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload.class);
        RequestTask requestTask = mock(RequestTask.class);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(withholdingOfAllowancesWithdrawalService).applyCloseAction(requestTask, taskActionPayload);
        verify(workflowService).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_OUTCOME, WithholdingOfAllowancesWithdrawalOutcome.CLOSED)
        );
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> types = handler.getTypes();
        assertEquals(Collections.singletonList(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION), types);
    }
}
