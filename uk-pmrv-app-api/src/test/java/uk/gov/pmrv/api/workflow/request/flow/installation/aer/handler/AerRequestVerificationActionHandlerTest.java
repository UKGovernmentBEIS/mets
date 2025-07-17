package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerSubmitService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerRequestVerificationActionHandlerTest {

    @InjectMocks
    private AerRequestVerificationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAerSubmitService requestAerSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final AerApplicationRequestVerificationRequestTaskActionPayload payload = AerApplicationRequestVerificationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AER_REQUEST_VERIFICATION_PAYLOAD).build();
        final String processId = "processId";
        final String requestId = "requestId";
        final LocalDate dueDate = LocalDate.now();
        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .dueDate(dueDate)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AER_REQUEST_VERIFICATION, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(requestAerSubmitService, times(1)).sendToVerifier(payload, task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.AER_OUTCOME, AerOutcome.VERIFICATION_REQUESTED));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.AER_REQUEST_VERIFICATION), handler.getTypes());
    }
}
