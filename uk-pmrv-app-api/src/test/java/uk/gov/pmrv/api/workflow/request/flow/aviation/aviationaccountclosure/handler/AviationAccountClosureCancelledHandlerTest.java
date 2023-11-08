package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class AviationAccountClosureCancelledHandlerTest {

	@InjectMocks
    private AviationAccountClosureCancelledHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestAviationAccountClosureService requestAviationAccountClosureService;

    @Test
    void process() {

        PmrvUser pmrvUser = PmrvUser.builder().build();
        String processTaskId = "processTaskId";
        Request request = Request.builder().id("id").build();
        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        verify(requestAviationAccountClosureService, times(1)).cancel("id");
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION);
    }
}
