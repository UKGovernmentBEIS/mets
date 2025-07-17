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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSubmitOutcome;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesCancelApplicationActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WithholdingOfAllowancesCancelApplicationActionHandler handler;

    @Test
    void process() {
        Long requestTaskId = 123L;
        RequestTaskActionType requestTaskActionType =
            RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION;
        AppUser appUser = new AppUser();
        RequestTaskActionEmptyPayload taskActionPayload = new RequestTaskActionEmptyPayload();

        RequestTask requestTask = new RequestTask();
        requestTask.setProcessTaskId("processTaskId");

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(workflowService).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME,
                WithholdingOfAllowancesSubmitOutcome.CANCELLED)
        );
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> expectedTypes =
            List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION);

        List<RequestTaskActionType> types = handler.getTypes();

        assertEquals(expectedTypes, types);
    }
}
