package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerVerificationReturnedToOperatorRequestActionPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AerVerificationReturnToOperatorActionHandlerTest {

    @InjectMocks
    private AerVerificationReturnToOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final AerVerificationReturnToOperatorRequestTaskActionPayload payload = AerVerificationReturnToOperatorRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.AER_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD)
                .changesRequired("Changes required")
                .build();

        final AerVerificationReturnedToOperatorRequestActionPayload actionPayload = AerVerificationReturnedToOperatorRequestActionPayload
                .builder()
                .changesRequired("Changes required")
                .payloadType(RequestActionPayloadType.AER_VERIFICATION_RETURNED_TO_OPERATOR_PAYLOAD)
                .build();

        final String processId = "processId";
        final String requestId = "requestId";

        final Request request = Request
                .builder()
                .type(RequestType.AER)
                .build();

        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .request(request)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.AER_VERIFICATION_RETURN_TO_OPERATOR, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.AER_VERIFICATION_RETURNED_TO_OPERATOR,user.getUserId());
        verify(workflowService, times(1)).completeTask(processId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsAnyElementsOf(List.of(RequestTaskActionType.AER_VERIFICATION_RETURN_TO_OPERATOR));
    }
}
