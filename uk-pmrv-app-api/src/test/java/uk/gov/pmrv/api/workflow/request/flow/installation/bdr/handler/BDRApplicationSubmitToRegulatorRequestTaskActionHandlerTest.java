package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSubmitService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationSubmitToRegulatorRequestTaskActionHandlerTest {


    @InjectMocks
    private BDRApplicationSubmitToRegulatorRequestTaskActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRSubmitService bdrSubmitService;

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

        handler.process(taskId, RequestTaskActionType.BDR_SUBMIT_TO_REGULATOR, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(bdrSubmitService, times(1)).submitToRegulator(task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.BDR_OUTCOME, BDROutcome.SUBMITTED_TO_REGULATOR));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.BDR_SUBMIT_TO_REGULATOR), handler.getTypes());
    }
}
