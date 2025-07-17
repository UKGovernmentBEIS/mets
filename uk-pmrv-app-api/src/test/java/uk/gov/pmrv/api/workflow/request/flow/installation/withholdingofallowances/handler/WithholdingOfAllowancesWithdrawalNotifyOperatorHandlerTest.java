package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawalService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesWithdrawnValidator;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawalNotifyOperatorHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WithholdingOfAllowancesWithdrawalService withholdingOfAllowancesWithdrawalService;

    @Mock
    private WithholdingOfAllowancesWithdrawnValidator withholdingOfAllowancesWithdrawnValidator;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawalNotifyOperatorHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        AppUser appUser = new AppUser();
        NotifyOperatorForDecisionRequestTaskActionPayload actionPayload = new NotifyOperatorForDecisionRequestTaskActionPayload();
        RequestTask requestTask = new RequestTask();
        requestTask.setProcessTaskId("processTaskId");
        Request request = new Request();
        request.setId("requestId");
        requestTask.setRequest(request);

        Map<String, Object> expectedCompleteTaskVariables = Map.of(
            BpmnProcessConstants.REQUEST_ID, "requestId",
            BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_OUTCOME, WithholdingOfAllowancesWithdrawalOutcome.WITHDRAWN
        );

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(
            requestTaskId,
            WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION,
            appUser,
            actionPayload
        );

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(withholdingOfAllowancesWithdrawnValidator).validate(requestTask, actionPayload, appUser);
        verify(withholdingOfAllowancesWithdrawalService).saveWithholdingOfAllowancesWithdrawalDecisionNotification(actionPayload, requestTask);
        verify(workflowService).completeTask("processTaskId", expectedCompleteTaskVariables);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actionTypes = actionHandler.getTypes();
        Assertions.assertEquals(List.of(WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION), actionTypes);
    }
}
