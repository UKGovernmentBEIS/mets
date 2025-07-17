package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerRequestVerificationRecallActionHandlerTest {

    @InjectMocks
    private AviationAerRequestVerificationRecallActionHandler recallActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String requestId = "REQ-ID";
        AppUser user = AppUser.builder().userId("userId").build();
        RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder()
            .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().id(requestId).build())
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        recallActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION, user, taskActionEmptyPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, RequestActionType.AVIATION_AER_RECALLED_FROM_VERIFICATION, user.getUserId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(recallActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION);
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.AVIATION_AER_RECALLED_FROM_VERIFICATION, recallActionHandler.getRequestActionType());
    }
}