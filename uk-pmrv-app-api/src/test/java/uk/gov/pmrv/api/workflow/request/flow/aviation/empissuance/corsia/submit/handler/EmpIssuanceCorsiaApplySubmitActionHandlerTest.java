package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.service.RequestEmpCorsiaService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaApplySubmitActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaApplySubmitActionHandler empIssuanceCorsiaApplySubmitActionHandler;

    @Mock
    private RequestEmpCorsiaService requestEmpCorsiaService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        Request request = Request.builder().id("requestId").build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).processTaskId(processTaskId).build();
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        empIssuanceCorsiaApplySubmitActionHandler.process(requestTaskId, RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION, user, emptyPayload);

        assertNotNull(request.getSubmissionDate());

        verify(requestEmpCorsiaService, times(1)).applySubmitAction(requestTask, user);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId());
    }

    @Test
    void getTypes() {
        assertThat(empIssuanceCorsiaApplySubmitActionHandler.getTypes()).containsOnly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION);
    }
}