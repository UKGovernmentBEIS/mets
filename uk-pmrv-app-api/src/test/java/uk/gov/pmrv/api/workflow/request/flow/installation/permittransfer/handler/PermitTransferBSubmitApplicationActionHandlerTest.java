package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBApplyService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBSubmitApplicationActionHandlerTest {

    @InjectMocks
    private PermitTransferBSubmitApplicationActionHandler handler;

    @Mock
    private PermitTransferBApplyService applyService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DateService dateService;

    @Test
    void process() {

        final RequestTaskActionEmptyPayload submitPayload =
            RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder()
                .id(1L)
                .request(request)
                .payload(PermitTransferBApplicationRequestTaskPayload.builder().build())
                .processTaskId(processTaskId)
                .build();
        final LocalDateTime submissionDate = LocalDateTime.of(2023, 1, 2, 3, 4);

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(dateService.getLocalDateTime()).thenReturn(submissionDate);

        //invoke
        handler.process(
            requestTask.getId(),
            RequestTaskActionType.PERMIT_TRANSFER_B_SUBMIT_APPLICATION,
            appUser,
            submitPayload
        );

        assertThat(request.getSubmissionDate()).isEqualTo(submissionDate);
        
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(dateService, times(1)).getLocalDateTime();
        verify(applyService, times(1)).applySubmitAction(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(
                BpmnProcessConstants.PERMIT_TRANSFER_SUBMIT_OUTCOME, PermitTransferOutcome.SUBMITTED
            ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_TRANSFER_B_SUBMIT_APPLICATION);
    }
}