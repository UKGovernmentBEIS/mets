package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesSubmissionValidator;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesNotifyOperatorHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WithholdingOfAllowancesService withholdingOfAllowancesService;

    @Mock
    private WithholdingOfAllowancesSubmissionValidator withholdingOfAllowancesSubmissionValidator;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WithholdingOfAllowancesNotifyOperatorHandler actionHandler;

    @Test
    void process_shouldThrowBusinessExceptionWhenUsersAreInvalid() {
        Long requestTaskId = 123L;
        RequestTaskActionType requestTaskActionType =
            RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION;
        PmrvUser pmrvUser = mock(PmrvUser.class);
        NotifyOperatorForDecisionRequestTaskActionPayload payload =
            mock(NotifyOperatorForDecisionRequestTaskActionPayload.class);
        RequestTask requestTask = mock(RequestTask.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION))
            .when(withholdingOfAllowancesSubmissionValidator)
            .validate(requestTask, payload, pmrvUser);

        assertThrows(BusinessException.class,
            () -> actionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, payload));
    }

    @Test
    void process() {
        long requestTaskId = 123L;
        long requestId = 1L;
        RequestTaskActionType requestTaskActionType =
            RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION;
        PmrvUser pmrvUser = mock(PmrvUser.class);
        NotifyOperatorForDecisionRequestTaskActionPayload payload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        RequestTask requestTask = mock(RequestTask.class);
        Request request = mock(Request.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTask.getRequest()).thenReturn(request);
        when(request.getId()).thenReturn(Long.toString(requestId));
        when(requestTask.getProcessTaskId()).thenReturn("processTaskId");

        actionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);

        verify(workflowService).completeTask("processTaskId", Map.of(
            BpmnProcessConstants.REQUEST_ID, Long.toString(requestId),
            BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME,
            WithholdingOfAllowancesSubmitOutcome.SUBMITTED
        ));
        verify(withholdingOfAllowancesService).saveWithholdingOfAllowancesDecisionNotification(payload, requestTask);
        verify(withholdingOfAllowancesSubmissionValidator).validate(requestTask, payload, pmrvUser);
    }

    @Test
    void getTypes_shouldReturnListOfWithholdingOfAllowancesNotifyOperatorType() {
        List<RequestTaskActionType> types = actionHandler.getTypes();
        assertEquals(List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION), types);
    }
}
