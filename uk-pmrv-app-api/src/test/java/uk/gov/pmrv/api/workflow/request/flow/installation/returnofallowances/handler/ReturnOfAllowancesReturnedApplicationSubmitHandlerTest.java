package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesReturnedService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesReturnedApplicationSubmitHandlerTest {

    @Mock
    private ReturnOfAllowancesReturnedService returnOfAllowancesReturnedService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private ReturnOfAllowancesReturnedApplicationSubmitHandler handler;

    @Test
    void process() {
        Long requestTaskId = 123L;
        String processTaskId = "process";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SUBMIT_APPLICATION;
        AppUser appUser = mock(AppUser.class);
        RequestTask requestTask = mock(RequestTask.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTask.getProcessTaskId()).thenReturn(processTaskId);

        handler.process(requestTaskId, requestTaskActionType, appUser, RequestTaskActionEmptyPayload.builder().build());

        verify(returnOfAllowancesReturnedService).submit(requestTask);
        verify(workflowService).completeTask(requestTask.getProcessTaskId());
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> types = handler.getTypes();
        assertEquals(List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SUBMIT_APPLICATION), types);
    }

}