package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReturnedToOperatorRequestActionPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERApplicationVerificationReturnToOperatorActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private NERApplicationVerificationReturnToOperatorActionHandler handler;

    @Test
    void process_shouldAddActionAndCompleteTask() {
        Long requestTaskId = 1L;

        RequestTask requestTask = mock(RequestTask.class);
        Request request = mock(Request.class);
        AppUser appUser = mock(AppUser.class);
        NERApplicationVerificationReturnToOperatorRequestTaskActionPayload payload =
                mock(NERApplicationVerificationReturnToOperatorRequestTaskActionPayload.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTask.getProcessTaskId()).thenReturn("process-task-id");
        when(appUser.getUserId()).thenReturn("user-id");

        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_VERIFICATION_RETURN_TO_OPERATOR,
                appUser,
                payload
        );

        verify(requestService).addActionToRequest(
                eq(request),
                any(NERVerificationReturnedToOperatorRequestActionPayload.class),
                eq(RequestActionType.NER_VERIFICATION_RETURNED_TO_OPERATOR),
                eq("user-id")
        );

        verify(workflowService).completeTask("process-task-id");
    }

    @Test
    void getTypes_shouldReturnCorrectType() {
        List<RequestTaskActionType> result = handler.getTypes();

        assertEquals(1, result.size());
        assertEquals(RequestTaskActionType.NER_VERIFICATION_RETURN_TO_OPERATOR, result.get(0));
    }
}
