package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestRecallEmailNotificationHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERApplicationVerificationRecallActionHandlerTest {

    @InjectMocks
    private NERApplicationVerificationRecallActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private List<RequestRecallEmailNotificationHandler> handlers;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final NerRequestPayload requestPayload = NerRequestPayload.builder()
                .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD).build();
        final String processId = "processId";
        final String requestId = "requestId";
        final RequestTask task = RequestTask.builder()
                .request(Request.builder()
                        .id(requestId)
                        .payload(requestPayload)
                        .build())
                .processTaskId(processId)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.NER_RECALL_FROM_VERIFICATION, user, payload);

        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(requestService, times(1))
                .addActionToRequest(task.getRequest(), null, RequestActionType.NER_RECALLED_FROM_VERIFICATION, user.getUserId());
        verify(workflowService, times(1))
                .completeTask(processId);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.NER_RECALL_FROM_VERIFICATION), handler.getTypes());
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.NER_RECALLED_FROM_VERIFICATION, handler.getRequestActionType());
    }
}
