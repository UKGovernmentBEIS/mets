package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;


@ExtendWith(MockitoExtension.class)
class AviationAccountClosureSubmitActionHandlerTest {

	@InjectMocks
    private AviationAccountClosureSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAccountClosureService requestAviationAccountClosureService;

    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestQueryService requestQueryService;


    @Test
    void process() {

        RequestTaskActionEmptyPayload taskActionPayload =
            RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
                .build();
        AppUser appUser = AppUser.builder().build();
        String processTaskId = "processTaskId";
        Request request1 = Request.builder().id("1").accountId(100L).build();
        Request request2 = Request.builder().id("2").accountId(200L).build();
        RequestTask requestTask =
            RequestTask.builder().id(1L).request(request1).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(requestQueryService.findInProgressRequestsByAccount(100L))
        .thenReturn(List.of(request1, request2));

        handler.process(requestTask.getId(),
            RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_APPLICATION,
            appUser,
            taskActionPayload);

        assertThat(request1.getSubmissionDate()).isNotNull();
        verify(requestAviationAccountClosureService, times(1)).applySubmitAction(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId);
        verify(workflowService, times(2)).deleteProcessInstance(
        		null, "Workflow terminated by the system because the account was closed");
        assertEquals(RequestStatus.CANCELLED, request2.getStatus());
    }
}
