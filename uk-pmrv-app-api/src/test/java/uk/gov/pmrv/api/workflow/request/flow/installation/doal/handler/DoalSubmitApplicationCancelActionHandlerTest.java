package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalSubmitOutcome;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitApplicationCancelActionHandlerTest {

    @InjectMocks
    private DoalSubmitApplicationCancelActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        final RequestTaskActionEmptyPayload cancelPayload = RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder().id(1L).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.DOAL_CANCEL_APPLICATION, appUser, cancelPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.DOAL_SUBMIT_OUTCOME, DoalSubmitOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.DOAL_CANCEL_APPLICATION);
    }
}
