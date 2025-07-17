package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferOutcome;

@ExtendWith(MockitoExtension.class)
class PermitTransferCancelActionHandlerTest {

    @InjectMocks
    private PermitTransferCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_TRANSFER_CANCEL_APPLICATION,
            appUser,
            RequestTaskActionEmptyPayload.builder().build());

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.PERMIT_TRANSFER_SUBMIT_OUTCOME, PermitTransferOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_TRANSFER_CANCEL_APPLICATION);
    }
}