package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirApplyService;

@ExtendWith(MockitoExtension.class)
class AirSubmitActionHandlerTest {

    @InjectMocks
    private AirSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AirApplyService applyService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        
        final String processId = "processId";
        RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .processTaskId(processId)
                .request(Request.builder().build())
                .build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.AIR_SUBMIT_APPLICATION, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(applyService, times(1)).applySubmitAction(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processId);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actual = handler.getTypes();

        assertThat(actual).isEqualTo(List.of(RequestTaskActionType.AIR_SUBMIT_APPLICATION));
    }
}
