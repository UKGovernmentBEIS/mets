package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.service.RequestPermitApplyService;

@ExtendWith(MockitoExtension.class)
class PermitApplySubmitActionHandlerTest {

    @InjectMocks
    private PermitApplySubmitActionHandler handler;

    @Mock
    private RequestPermitApplyService requestPermitApplyService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void doProcess() {
        RequestTaskActionEmptyPayload permitApplySubmitPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        AppUser appUser = AppUser.builder().build();
        String processTaskId = "processTaskId";
        Request request = Request.builder().id("1").build();
        RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION, appUser, permitApplySubmitPayload);

        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestPermitApplyService, times(1)).applySubmitAction(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }
}
