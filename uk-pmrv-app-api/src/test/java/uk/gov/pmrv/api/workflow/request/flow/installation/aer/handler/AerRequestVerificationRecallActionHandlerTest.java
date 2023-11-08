package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerRequestVerificationRecallActionHandlerTest {

    @InjectMocks
    private AerRequestVerificationRecallActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD).build();
        final String processId = "processId";
        final String requestId = "requestId";
        final RequestTask task = RequestTask.builder()
                .request(Request.builder()
                        .id(requestId)
                        .payload(aerRequestPayload)
                        .build())
                .processTaskId(processId)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AER_RECALL_FROM_VERIFICATION, user, payload);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(requestService, times(1))
                .addActionToRequest(task.getRequest(), null, RequestActionType.AER_RECALLED_FROM_VERIFICATION, user.getUserId());
        verify(workflowService, times(1))
                .completeTask(processId);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.AER_RECALL_FROM_VERIFICATION), handler.getTypes());
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.AER_RECALLED_FROM_VERIFICATION, handler.getRequestActionType());
    }
}