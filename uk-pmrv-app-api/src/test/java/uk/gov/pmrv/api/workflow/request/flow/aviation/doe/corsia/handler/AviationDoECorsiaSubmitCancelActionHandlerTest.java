package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmitCancelActionHandlerTest {

    @InjectMocks
    private AviationDoECorsiaSubmitCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_CANCEL;
        AppUser appUser = AppUser.builder().userId("user").build();
        RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
                .build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactlyInAnyOrder(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_CANCEL);
    }
}
