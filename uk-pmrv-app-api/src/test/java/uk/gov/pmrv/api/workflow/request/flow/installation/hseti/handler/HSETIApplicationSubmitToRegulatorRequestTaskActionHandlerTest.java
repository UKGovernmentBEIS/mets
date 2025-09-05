package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETISubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISubmitService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationSubmitToRegulatorRequestTaskActionHandlerTest {

    @InjectMocks
    private HSETIApplicationSubmitToRegulatorRequestTaskActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private HSETISubmitService hsetiSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final String processId = "processId";
        final String requestId = "requestId";
        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.HSE_TI_SUBMIT_TO_REGULATOR, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(hsetiSubmitService, times(1)).submitToRegulator(task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.HSE_TI_SUBMIT_OUTCOME, HSETISubmitOutcome.SUBMITTED));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.HSE_TI_SUBMIT_TO_REGULATOR), handler.getTypes());
    }

}
